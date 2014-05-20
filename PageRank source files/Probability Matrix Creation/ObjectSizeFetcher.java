import java.lang.instrument.Instrumentation;
import java.util.Properties;

public class ObjectSizeFetcher {
	private static final String KEY = "my.instrumentation";
    public static void premain(String options, Instrumentation inst) {
        Properties props = System.getProperties();
        if(props.get(KEY) == null)
           props.put(KEY, inst);
    }

    public static Instrumentation getInstrumentation() { 
       return (Instrumentation) System.getProperties().get(KEY);
    }

    public static long getObjectSize(Object o) {
    	Instrumentation ins = getInstrumentation();
        return ins.getObjectSize(o);
    }
}