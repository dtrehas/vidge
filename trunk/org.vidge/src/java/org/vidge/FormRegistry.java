package org.vidge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.langcom.locale.LocalizedStringPart;
import org.vidge.explorer.FormExplorer;
import org.vidge.explorer.ObjectExplorer;
import org.vidge.form.LocaleForm;
import org.vidge.form.LocalizedStringPartForm2;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IForm;
import org.vidge.inface.IRegistryRequestListener;
import org.vidge.util.FormContext;

@SuppressWarnings({
		"unchecked", "rawtypes"
})
public class FormRegistry {

	private final static Map<Class<?>, IEntityExplorer> registry = new HashMap<Class<?>, IEntityExplorer>();
	private final static Map<String, Map<Class<?>, IEntityExplorer>> contextRegistry = new HashMap<String, Map<Class<?>, IEntityExplorer>>();
	private static String defaultContext = null;
	private static final List<IRegistryRequestListener> listenerList = new ArrayList<IRegistryRequestListener>();
	static {
		registerForm(Locale.class, LocaleForm.class);
		registerForm(LocalizedStringPart.class, LocalizedStringPartForm2.class);
	}

	private FormRegistry() {
	}

	public static void setDefaultContext(String context) {
		defaultContext = context;
	}

	public static <T> void registerContextExplorer(String context, Class<? extends T> klass, IEntityExplorer explorer) {
		Map<Class<?>, IEntityExplorer> map = contextRegistry.get(context);
		if (map == null) {
			map = new HashMap<Class<?>, IEntityExplorer>();
			contextRegistry.put(context, map);
		}
		map.put(klass, explorer);
		registry.put(klass, explorer);
	}

	public static <T> void registerExplorer(Class<? extends T> klass, IEntityExplorer explorer) {
		registerContextExplorer(null, klass, explorer);
	}

	public static <T> void registerForm(Class<? extends T> klass, Class<? extends IForm<? extends T>> form) {
		registerContextExplorer(null, klass, new FormExplorer(form));
	}

	public static <T> void registerContextForm(String context, Class<? extends T> klass, Class<? extends IForm<? extends T>> form) {
		registerContextExplorer(context, klass, new FormExplorer(form));
	}

	public static IEntityExplorer getEntityExplorer(String context, Class<?> klass) {
		fireListenersChanged(context, klass);
		Map<Class<?>, IEntityExplorer> map = contextRegistry.get(context);
		if (map == null && context.equalsIgnoreCase(FormContext.CREATE.name())) {
			map = contextRegistry.get(FormContext.EDIT.name());
		}
		IEntityExplorer result = null;
		if (map != null) {
			result = map.get(klass);
		}
		if (result == null) {
			result = searchSimilar(klass);
		}
		if (result == null) {
			result = new ObjectExplorer(klass);
			// return null;
		}
		return result;
	}

	private static IEntityExplorer searchSimilar(Class<?> klass) {
		for (Map.Entry<Class<?>, IEntityExplorer> entry : registry.entrySet()) {
			if (entry.getKey().isAssignableFrom(klass)) {
				return entry.getValue();
			}
		}
		return null;
	}

	public static IEntityExplorer getEntityExplorer(Class<?> klass) {
		return getEntityExplorer(defaultContext, klass);
	}

	private static void fireListenersChanged(String context, Class<?> klass) {
		for (IRegistryRequestListener listener : listenerList) {
			listener.request(context, klass);
		}
	}

	public static boolean addListener(IRegistryRequestListener e) {
		return listenerList.add(e);
	}
}
