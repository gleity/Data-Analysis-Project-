package main.java;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PostConstructAdapterFactory implements TypeAdapterFactory {
    // copied from https://gist.github.com/swankjesse/20df26adaf639ed7fd160f145a0b661a
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        for (Class<?> t = type.getRawType(); (t != Object.class) && (t.getSuperclass() != null); t = t.getSuperclass()) {
            for (Method m : t.getDeclaredMethods()) {
                if (m.isAnnotationPresent(PostConstruct.class)) {
                    m.setAccessible(true);
                    TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
                    return new PostConstructAdapter<T>(delegate, m);
                }
            }
        }
        return null;
    }

    final static class PostConstructAdapter<T> extends TypeAdapter<T> {
        private final TypeAdapter<T> delegate;
        private final Method method;

        public PostConstructAdapter(TypeAdapter<T> delegate, Method method) {
            this.delegate = delegate;
            this.method = method;
        }

        @Override public T read(JsonReader in) throws IOException {
            T result = delegate.read(in);
            if (result != null) {
                try {
                    method.invoke(result);
                } catch (IllegalAccessException e) {
                    throw new AssertionError();
                } catch (InvocationTargetException e) {
                    if (e.getCause() instanceof RuntimeException) throw (RuntimeException) e.getCause();
                    throw new RuntimeException(e.getCause());
                }
            }
            return result;
        }

        @Override public void write(JsonWriter out, T value) throws IOException {
            delegate.write(out, value);
        }
    }
}