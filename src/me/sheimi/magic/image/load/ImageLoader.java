/*  ImageLoader.java
 *
 *  Load image from file
 *
 *  @author Reason Zhang
 *  @email  sheimi.zhang@gmail.com
 *  
 *  CopyRight (c) 2012 Magic Team
 *
 */
package me.sheimi.magic.image.load;

import java.util.*;
import java.io.*;
import me.sheimi.util.*;
import me.sheimi.magic.image.*;

public abstract class ImageLoader implements Iterator<Image> {
  protected boolean end = false;
  public abstract Image next();
  public abstract void close();
  public boolean hasNext() {
    return !end;
  }
  public void remove() {
  }

  private static Map<String, Class<? extends ImageLoader>> loaders;
  static {
    loaders = new HashMap<String, Class<? extends ImageLoader>>();
    loaders.put("seq", SeqImageLoader.class);
    loaders.put("tar", TarImageLoader.class);
  }
  public static ImageLoader getLoader(String filename) {
    String[] fns = filename.split("\\.");
    String subfix = fns[fns.length - 1];
    Class<? extends ImageLoader> loaderClz = loaders.get(subfix);
    ImageLoader loader = ReflectionUtils.newInstance(loaderClz, filename);
    return loader;
  }

}
