package gov.nih.nci.cagrid.syncgts.core;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.syncgts.bean.DateFilter;
import gov.nih.nci.cagrid.syncgts.bean.SyncReport;

import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.namespace.QName;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class HistoryManager {

	private final static QName reportQN = new QName(SyncGTSDefault.SYNC_GTS_NAMESPACE, "SyncReport");
	public static int maxSyncReports = 150;


	public File addReport(SyncReport report) throws Exception {
		File r = getFile(report.getTimestamp());
		Utils.serializeDocument(r.getAbsolutePath(), report, reportQN);
		return r;
	}


	public SyncReport getLastReport() throws Exception {
		File dir = getLastestDayDir();
		if (dir != null) {
			File latest = null;
			int num = -1;
			File files[] = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {

					int index = files[i].getName().indexOf(".xml");
					if (index != -1) {
						try {
							int temp = Integer.valueOf(files[i].getName().substring(0, index)).intValue();
							if (temp > num) {
								num = temp;
								latest = files[i];
							}
						} catch (NumberFormatException e) {

						}
					}

				}
			}
			if (latest != null) {
				return getReport(latest.getAbsolutePath());
			}

		}
		return null;
	}


	private File getEarliestDayDir() {
		return getEarliestDir(getEarliestMonthDir());
	}


	private File getEarliestMonthDir() {
		return getEarliestDir(getEarliestYearDir());
	}


	private File getEarliestYearDir() {
		return getEarliestDir(getHistoryDirectory());
	}


	private File getEarliestDir(File dir) {
		File earliest = null;
		int num = Integer.MAX_VALUE;
		if (dir != null) {
			if (dir.exists() && dir.isDirectory()) {
				File[] dirs = dir.listFiles();
				for (int i = 0; i < dirs.length; i++) {
					if (dirs[i].isDirectory()) {
						try {
							int temp = Integer.valueOf(dirs[i].getName()).intValue();
							if (temp < num) {
								num = temp;
								earliest = dirs[i];
							}
						} catch (NumberFormatException e) {

						}
					}
				}

			}
		}
		return earliest;
	}


	private File getLastestDayDir() {
		return getLatestDir(getLatestMonthDir());
	}


	private File getLatestMonthDir() {
		return getLatestDir(getLatestYearDir());
	}


	private File getLatestYearDir() {
		return getLatestDir(getHistoryDirectory());
	}


	private File getLatestDir(File dir) {
		File latest = null;
		int num = -1;
		if (dir != null) {
			if (dir.exists() && dir.isDirectory()) {
				File[] dirs = dir.listFiles();
				for (int i = 0; i < dirs.length; i++) {
					if (dirs[i].isDirectory()) {
						try {
							int temp = Integer.valueOf(dirs[i].getName()).intValue();
							if (temp > num) {
								num = temp;
								latest = dirs[i];
							}
						} catch (NumberFormatException e) {

						}
					}
				}

			}
		}
		return latest;
	}


	public SyncReport getReport(String fileName) throws Exception {
		return (SyncReport) Utils.deserializeDocument(fileName, SyncReport.class);
	}


	private DateFilter getEarliestFitler() throws Exception {
		File day = getEarliestDayDir();
		File month = day.getParentFile();
		File year = month.getParentFile();

		if ((day == null) || (month == null) || (year == null)) {
			return null;
		} else {
			DateFilter d = new DateFilter();
			d.setDay(Integer.valueOf(day.getName()).intValue());
			d.setMonth(Integer.valueOf(month.getName()).intValue());
			d.setYear(Integer.valueOf(year.getName()).intValue());
			return d;
		}
	}

	private boolean isAfter(DateFilter start, DateFilter end){
		if(start.getYear()<=end.getYear()){
			if(start.getMonth()<=end.getMonth()){
				if(start.getDay()<=end.getDay()){
					return false;
				}else{
					return true;
				}
			}else{
				return true;
			}
		}else{
			return true;
		}
	}

	public void prune(DateFilter filter) throws Exception {
		DateFilter start = getEarliestFitler();
		if(start==null){
			return;
		}

		Calendar c = new GregorianCalendar();
		c.add(Calendar.YEAR, (filter.getYear() * -1));
		c.add(Calendar.MONTH, (filter.getMonth() * -1));
		c.add(Calendar.DAY_OF_MONTH, (filter.getDay() * -1));
		DateFilter end = new DateFilter();
		end.setDay(c.get(Calendar.DAY_OF_MONTH));
		end.setMonth(c.get(Calendar.MONTH) + 1);
		end.setYear(c.get(Calendar.YEAR));

		while (!isAfter(start, end)) {
			File startDir = getDirectory(start);
			if ((startDir.exists()) && (startDir.isDirectory())) {
				File[] fileList = startDir.listFiles();
				for (int i = 0; i < fileList.length; i++) {
					fileList[i].delete();
				}
				File monthDir = startDir.getParentFile();
				File yearDir = monthDir.getParentFile();
				startDir.delete();
				deleteIfEmpty(monthDir);
				deleteIfEmpty(yearDir);

			}
			this.incrementDate(start);
		}
	}


	private void deleteIfEmpty(File dir) {
		if (dir.isDirectory()) {
			File files[] = dir.listFiles();
			if ((files == null) || (files.length == 0)) {
				dir.delete();
			}
		}
	}


	public SyncReport[] search(DateFilter startDate, DateFilter end) throws Exception {
		DateFilter start = new DateFilter();
		start.setDay(startDate.getDay());
		start.setMonth(startDate.getMonth());
		start.setYear(startDate.getYear());
		SyncReport[] reports = new SyncReport[maxSyncReports];
		int checkMax = 0;
		int iterator = 0;
		this.incrementDate(end);
		while (!start.equals(end)) {
			File startDir = getDirectory(start);
			if ((startDir.exists()) && (startDir.isDirectory())) {
				String[] fileList = startDir.list();
				checkMax = checkMax + fileList.length;
				if (checkMax > maxSyncReports)
					throw new Exception();
				else {
					for (int i = 0; i < fileList.length; i++) {
						File inputFile = new File(startDir.getAbsolutePath() + File.separator + fileList[i]);
						FileReader in = new FileReader(inputFile);
						if (in.read() != -1) {
							reports[iterator] = this.getReport(startDir.getAbsolutePath() + File.separator
								+ fileList[i]);
							iterator++;
						} else
							throw new Exception();
						in.close();
					}
				}
			}
			this.incrementDate(start);
		}
		SyncReport[] returnReports = new SyncReport[checkMax];
		System.arraycopy(reports, 0, returnReports, 0, returnReports.length);

		return returnReports;
	}


	public SyncReport[] search(DateFilter startDate, DateFilter end, File histDir) throws Exception {
		DateFilter start = new DateFilter();
		start.setDay(startDate.getDay());
		start.setMonth(startDate.getMonth());
		start.setYear(startDate.getYear());
		SyncReport[] reports = new SyncReport[maxSyncReports];
		int checkMax = 0;
		int iterator = 0;
		this.incrementDate(end);
		while (!start.equals(end)) {
			File startDir = getDirectory(start, histDir);
			if ((startDir.exists()) && (startDir.isDirectory())) {
				String[] fileList = startDir.list();
				checkMax = checkMax + fileList.length;
				if (checkMax > maxSyncReports)
					throw new Exception();
				else {
					for (int i = 0; i < fileList.length; i++) {
						File inputFile = new File(startDir.getAbsolutePath() + File.separator + fileList[i]);
						FileReader in = new FileReader(inputFile);
						if (in.read() != -1) {
							reports[iterator] = this.getReport(startDir.getAbsolutePath() + File.separator
								+ fileList[i]);
							iterator++;
						} else
							throw new Exception();
						in.close();
					}
				}
			}
			this.incrementDate(start);
		}
		SyncReport[] returnReports = new SyncReport[checkMax];
		System.arraycopy(reports, 0, returnReports, 0, returnReports.length);

		return returnReports;
	}


	private File getFile(String timestamp) {
		File dir = getDirectory(timestamp);
		return new File(dir.getAbsolutePath() + File.separator + timestamp + ".xml");
	}


	private File getHistoryDirectory() {
		File dir = new File(SyncGTSDefault.getSyncGTSUserDir() + File.separator + "history");
		dir.mkdirs();
		return dir;
	}


	private File getDirectory(DateFilter f) {
		File histDir = getHistoryDirectory();
		String month;
		String day;
		if (f.getMonth() < 10) {
			month = "0" + f.getMonth();
		} else
			month = "" + f.getMonth();

		if (f.getDay() < 10) {
			day = "0" + f.getDay();
		} else
			day = "" + f.getDay();

		File dir = new File(histDir.getAbsolutePath() + File.separator + f.getYear() + File.separator + month
			+ File.separator + day);
		return dir;
	}


	private File getDirectory(DateFilter f, File histDir) {
		String month;
		String day;
		if (f.getMonth() < 10) {
			month = "0" + f.getMonth();
		} else
			month = "" + f.getMonth();

		if (f.getDay() < 10) {
			day = "0" + f.getDay();
		} else
			day = "" + f.getDay();

		File dir = new File(histDir.getAbsolutePath() + File.separator + f.getYear() + File.separator + month
			+ File.separator + day);
		return dir;
	}


	private File getDirectory(String timestamp) {
		String year = timestamp.substring(0, 4);
		String month = timestamp.substring(4, 6);
		String days = timestamp.substring(6, 8);
		File histDir = getHistoryDirectory();
		File dir = new File(histDir.getAbsolutePath() + File.separator + year + File.separator + month + File.separator
			+ days);
		dir.mkdirs();
		return dir;
	}


	private void incrementDate(DateFilter f) {
		if (f.getDay() >= 31) {
			f.setDay(1);
			if (f.getMonth() >= 12) {
				f.setMonth(1);
				f.setYear(f.getYear() + 1);
			} else {
				f.setMonth(f.getMonth() + 1);
			}
		} else
			f.setDay(f.getDay() + 1);

	}

}
