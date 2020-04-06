package com.github.nickid2018.chemistrylab.util;

public class LogUtils {

	// Clear logs
	public static long clearingLogTime = TimeUtils.getTime();

	public static void clearLog() {
		if (TimeUtils.getTime() - clearingLogTime < 1000)
			return;
//		LayerRender.pushLayer(layer = new PleaseWaitLayer(I18N.getString("dealing.log.clear"), () -> {
//			layer.isClickLegal(1);
//			File dir = new File(LogUtils.DEFAULT_LOG_FILE);
//			File[] todels = dir.listFiles((FilenameFilter) (dir1, name) -> !name.equals("ChemistryLab-Log"));
//			for (File del : todels) {
//				del.delete();
//			}
//			clearingLogTime = TimeUtils.getTime();
//			ChemistryLab.logger.info("Cleared Log.");
//			layer.setSuccess(I18N.getString("sidebar.log.success"));
//		}).start());
	}

	public static String DEFAULT_LOG_FILE;

}
