package me.sheimi.hbase.image;

import java.io.*;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.commons.logging.*;

import me.sheimi.hbase.*;
import me.sheimi.magic.image.*;
import me.sheimi.magic.image.store.*;
import me.sheimi.magic.image.meta.*;

public class RetrieveImage {

  private static final Log LOG = LogFactory.getLog(RetrieveImage.class);
  private static Configuration cfg = HBaseConfig.cfg;

  private ImageStorage storage;
  private FilterList filterList = new FilterList();
  public  RetrieveImage(ImageStorage storage) {
    this.storage = storage;
  }

  public void scanTable() {
    try {
      HTable table = new HTable(cfg, ImageSchema.TABLE_NAME);
      Scan scan = new Scan();
      scan.setFilter(filterList);
      ResultScanner rs = table.getScanner(scan);
      packImage(rs);
      table.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void packImage(ResultScanner rs) {
    LOG.info("Begin Pack Images ... ");
    for (Result r : rs) {
      byte[] filename = r.getValue(ImageSchema.FAMILY_META,
                                   ImageSchema.META_FILENAME);
      byte[] image_raw = r.getValue(ImageSchema.FAMILY_DATA,
                                    ImageSchema.DATA_IMAGE);
      Image image = new Image(image_raw, Bytes.toString(filename));
      storage.write(image);
    }
    storage.close();
    LOG.info("Image Pack Finished ... ");
  }

  public static void main(String [] args) {
    RetrieveImage ri = new RetrieveImage(ImageStorage.getStorage(args[0]));
    ri.scanTable();
  }

}
