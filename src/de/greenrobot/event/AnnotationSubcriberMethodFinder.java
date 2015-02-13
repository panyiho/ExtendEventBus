package de.greenrobot.event;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.util.Log;

public class AnnotationSubcriberMethodFinder extends SubscriberMethodFinder {

	
	AnnotationSubcriberMethodFinder(
			List<Class<?>> skipMethodVerificationForClassesList) {
		super(skipMethodVerificationForClassesList);
	}

	@Override
	public List<SubscriberMethod> findSubscriberMethods(
			Class<?> subscriberClass) {
		 String key = subscriberClass.getName();
	        List<SubscriberMethod> subscriberMethods;
	        synchronized (methodCache) {
	            subscriberMethods = methodCache.get(key);
	        }
	        if (subscriberMethods != null) {
	            return subscriberMethods;
	        }
	        subscriberMethods = new ArrayList<SubscriberMethod>();
	        Class<?> clazz = subscriberClass;
	        HashSet<String> eventTypesFound = new HashSet<String>();
	        StringBuilder methodKeyBuilder = new StringBuilder();
	        while (clazz != null) {
	            String name = clazz.getName();
	            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
	                // Skip system classes, this just degrades performance
	                break;
	            }

	            // Starting with EventBus 2.2 we enforced methods to be public (might change with annotations again)
	            Method[] methods = clazz.getMethods();
	            for (Method method : methods) {
	                Subscriber annotation = method.getAnnotation(Subscriber.class);
	                String methodName = method.getName();
	                if (annotation!=null) {
	                    int modifiers = method.getModifiers();
	                    if ((modifiers & Modifier.PUBLIC) != 0 && (modifiers & MODIFIERS_IGNORE) == 0) {
	                        Class<?>[] parameterTypes = method.getParameterTypes();
	                        if (parameterTypes.length == 1) {
	                            ThreadMode  threadMode = annotation.value();
	                            
	                            Class<?> eventType = parameterTypes[0];
	                            methodKeyBuilder.setLength(0);
	                            methodKeyBuilder.append(methodName);
	                            methodKeyBuilder.append('>').append(eventType.getName());
	                            String methodKey = methodKeyBuilder.toString();
	                            if (eventTypesFound.add(methodKey)) {
	                                // Only add if not already found in a sub class
	                                subscriberMethods.add(new SubscriberMethod(method, threadMode, eventType));
	                            }
	                        }
	                    } else if (!skipMethodVerificationForClasses.containsKey(clazz)) {
	                        Log.d(EventBus.TAG, "Skipping method (not public, static or abstract): " + clazz + "."
	                                + methodName);
	                    }
	                }
	            }
	            clazz = clazz.getSuperclass();
	        }
	        if (subscriberMethods.isEmpty()) {
	            throw new EventBusException("Subscriber " + subscriberClass + " has no public methods called "
	                    );
	        } else {
	            synchronized (methodCache) {
	                methodCache.put(key, subscriberMethods);
	            }
	            return subscriberMethods;
	        }
	}

}
