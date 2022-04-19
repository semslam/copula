package com.copulaapp.webservice.util.validator;

import com.copulaapp.webservice.util.validator.annotations.UsernameValidation;
import com.copulaapp.webservice.util.validator.annotations.EmailValidation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Created by heeleeaz on 8/13/16.
 */
public class Val {
    private static Class[] annotations = new Class<?>[]{EmailValidation.class, UsernameValidation.class};

    private static ValidationResult validator(Object obj) throws IllegalAccessException {
        Class<?> cls = obj.getClass();

        ValidationResult result = new ValidationResult();
        for (Field field : cls.getFields()) {
            Annotation annotation = getValidationAnnotation(field);
            if (annotation == null) continue;


            Class<?> annotationType = annotation.annotationType();
            if (annotationType == EmailValidation.class) {
                if (!emailValidation((String) field.get(obj)))
                    result.messages.add(((EmailValidation) annotation).message());
            } else if (annotationType == UsernameValidation.class) {
                if (!usernameValidation((String) field.get(obj)))
                    result.messages.add(((UsernameValidation) annotation).message());
            }

            if (result.messages.size() == 0) result.valid = true;
        }
        return result;
    }

    public static ValidationResult validate(Object obj) {
        try {
            return Val.validator(obj);
        } catch (Exception e) {
            return new ValidationResult();
        }
    }

    private static Annotation getValidationAnnotation(Field field) {
        Annotation[] fieldAnnotations = field.getAnnotations();
        for (Class<?> ann : annotations) {
            for (Annotation fieldAnn : fieldAnnotations) {
                if (ann == fieldAnn.annotationType()) return fieldAnn;
            }
        }
        return null;
    }

    public static boolean emailValidation(String text) {
        return EmailValidator.getInstance().isValid(text);
    }

    public static boolean usernameValidation(String text) {
        return EmailValidator.getInstance().isValidUser(text);
    }

    public static boolean textValidation(String text) {
        return true;
    }

    public static String parseUsername(String text) throws ValidationException {
        if (usernameValidation(text)) return text;
        throw new ValidationException("Invalid Username: " + text);
    }

    public static String parseEmail(String text) throws ValidationException {
        if (emailValidation(text)) return text;
        throw new ValidationException("Invalid Email");
    }

    public static String parseText(String text) throws ValidationException {
        if (textValidation(text)) return text;
        throw new ValidationException(text + " Input Not Valid");
    }

    public static String parseUser(String text) {
        if (usernameValidation(text) || emailValidation(text))
            return text;
        throw new ValidationException(text + " Input Not Valid");
    }

}
