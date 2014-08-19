package org.vidge;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.langcom.locale.LocalizedStringPart;
import org.vidge.explorer.FormExplorer;
import org.vidge.explorer.def.ColorExplorer;
import org.vidge.explorer.def.DateExplorer;
import org.vidge.explorer.def.FileExplorer;
import org.vidge.explorer.def.FontExplorer;
import org.vidge.explorer.def.ImageExplorer;
import org.vidge.explorer.def.StringExplorer;
import org.vidge.form.IForm;
import org.vidge.form.impl.LocaleForm;
import org.vidge.form.impl.LocalizedStringPartForm2;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IRegistryRequestListener;
import org.vidge.util.FormContext;
import org.vidge.util.TypeUtil;

@SuppressWarnings({
		"unchecked", "rawtypes"
})
public class FormRegistry {

	private final static Map<Class<?>, IEntityExplorer> registry = new HashMap<Class<?>, IEntityExplorer>();
	private final static Map<String, Map<Class<?>, IEntityExplorer>> contextRegistry = new HashMap<String, Map<Class<?>, IEntityExplorer>>();
	private static String defaultContext = "DEFAULT";
	private static final List<IRegistryRequestListener> listenerList = new ArrayList<IRegistryRequestListener>();
	private final static Map<Class<?>, FormExplorer> formRegistry = new HashMap<Class<?>, FormExplorer>();
	static {
		registerForm(Locale.class, LocaleForm.class);
		registerForm(LocalizedStringPart.class, LocalizedStringPartForm2.class);
		registerContextExplorer(null, String.class, new StringExplorer());
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
		registerContextExplorer(defaultContext, klass, explorer);
	}

	public static <T> void registerForm(Class<? extends T> klass, Class<? extends IForm<? extends T>> form) {
		registerContextExplorer(defaultContext, klass, getExplorer(form));
	}

	private static FormExplorer getExplorer(Class formClass) {
		FormExplorer explorer = formRegistry.get(formClass);
		if (explorer == null) {
			explorer = new FormExplorer(formClass);
			formRegistry.put(formClass, explorer);
		}
		return explorer;
	}

	public static <T> void registerContextForm(String context, Class<? extends T> klass, Class<? extends IForm<? extends T>> form) {
		registerContextExplorer(context, klass, getExplorer(form));
	}

	public static IEntityExplorer getEntityExplorer(String context, Object obj) {
		Class<?> klass = null;
		try {
			klass = TypeUtil.getClazz(obj);
		} catch (ClassNotFoundException e) {
			throw new VidgeException(e);
		}
		fireListenersChanged(context, klass);
		// if (TypeUtil.isInnerType(klass)) {
		// return getInnerExplorer(klass);
		// }
		Map<Class<?>, IEntityExplorer> map = contextRegistry.get(context);
		if (map == null && context.equalsIgnoreCase(FormContext.CREATE.name())) {
			map = contextRegistry.get(FormContext.EDIT.name());
		}
		if (map == null) {
			map = contextRegistry.get(defaultContext);
		}
		IEntityExplorer result = null;
		if (map != null) {
			result = map.get(klass);
		}
		if (result == null) {
			result = searchSimilar(klass);
		}
		if (result == null) {
			return null;
		}
		return result.copy();
	}

	private static IEntityExplorer getInnerExplorer(Class<?> klass) {
		if (klass.equals(Date.class)) {
			return new DateExplorer();
		} else if ((klass.equals(Color.class))) {
			return new ColorExplorer();
		} else if ((klass.equals(Image.class))) {
			return new ImageExplorer();
		} else if ((klass.equals(Font.class))) {
			return new FontExplorer();
		} else if ((klass.equals(File.class))) {
			return new FileExplorer();
		} else if ((klass.equals(String.class))) {
			return new StringExplorer();
		}
		return null;
	}

	/**
	 * Generates exception if entity explorer not found
	 * 
	 * @param context
	 * @param klass
	 * @return
	 */
	public static IEntityExplorer getEntityExplorerTh(String context, Class<?> klass) {
		IEntityExplorer result = getEntityExplorer(context, klass);
		if (result == null) {
			throw new VidgeException("*** Not found entity explorer for class - " + klass.getName());
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
