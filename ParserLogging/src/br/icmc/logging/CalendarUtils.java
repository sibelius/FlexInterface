package br.icmc.logging;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CalendarUtils {
	public static long difference(Calendar c1, Calendar c2, int unit) {

		differenceCheckUnit(unit);

		Map<Integer, Long> unitEstimates = differenceGetUnitEstimates();

		Calendar first = (Calendar) c1.clone();
		Calendar last = (Calendar) c2.clone();

		long difference = c2.getTimeInMillis() - c1.getTimeInMillis();

		long unitEstimate = unitEstimates.get(unit).longValue();
		long increment = (long) Math.floor((double) difference / (double) unitEstimate);
		increment = Math.max(increment, 1);

		long total = 0;

		while (increment > 0) {
			add(first, unit, increment);
			if (first.after(last)) {
				add(first, unit, increment * -1);
				increment = (long) Math.floor(increment / 2);
			} else {
				total += increment;
			}
		}

		return total;

	}

	private static Map<Integer, Long> differenceGetUnitEstimates() {
		Map<Integer, Long> unitEstimates = new HashMap<Integer, Long>();
		unitEstimates.put(Calendar.YEAR, 1000l * 60 * 60 * 24 * 365);
		unitEstimates.put(Calendar.MONTH, 1000l * 60 * 60 * 24 * 30);
		unitEstimates.put(Calendar.DAY_OF_MONTH, 1000l * 60 * 60 * 24);
		unitEstimates.put(Calendar.HOUR_OF_DAY, 1000l * 60 * 60);
		unitEstimates.put(Calendar.MINUTE, 1000l * 60);
		unitEstimates.put(Calendar.SECOND, 1000l);
		unitEstimates.put(Calendar.MILLISECOND, 1l);
		return unitEstimates;
	}

	private static void differenceCheckUnit(int unit) {
		List<Integer> validUnits = new ArrayList<Integer>();
		validUnits.add(Calendar.YEAR);
		validUnits.add(Calendar.MONTH);
		validUnits.add(Calendar.DAY_OF_MONTH);
		validUnits.add(Calendar.HOUR_OF_DAY);
		validUnits.add(Calendar.MINUTE);
		validUnits.add(Calendar.SECOND);
		validUnits.add(Calendar.MILLISECOND);

		if (!validUnits.contains(unit)) {
			throw new RuntimeException(
					"Invalid unit");
		}
	}

	public static void add(Calendar c, int unit, long increment) {
		while (increment > Integer.MAX_VALUE) {
			c.add(unit, Integer.MAX_VALUE);
			increment -= Integer.MAX_VALUE;
		}
		c.add(unit, (int) increment);
	}
}
