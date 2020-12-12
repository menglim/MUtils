package org.suorsdey;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class TimeAgo {
    public static final Map<String, Long> times = new LinkedHashMap<>();

    public static final Map<String, Long> timesAbb = new LinkedHashMap<>();

    static {
        times.put("year", TimeUnit.DAYS.toMillis(365));
        times.put("month", TimeUnit.DAYS.toMillis(30));
        times.put("week", TimeUnit.DAYS.toMillis(7));
        times.put("day", TimeUnit.DAYS.toMillis(1));
        times.put("hour", TimeUnit.HOURS.toMillis(1));
        times.put("minute", TimeUnit.MINUTES.toMillis(1));
        times.put("second", TimeUnit.SECONDS.toMillis(1));

        timesAbb.put("y", TimeUnit.DAYS.toMillis(365));
        timesAbb.put("m", TimeUnit.DAYS.toMillis(30));
        timesAbb.put("w", TimeUnit.DAYS.toMillis(7));
        timesAbb.put("d", TimeUnit.DAYS.toMillis(1));
        timesAbb.put("h", TimeUnit.HOURS.toMillis(1));
        timesAbb.put("mi", TimeUnit.MINUTES.toMillis(1));
        timesAbb.put("s", TimeUnit.SECONDS.toMillis(1));
    }

    public static String toRelative(long duration, int maxLevel) {
        StringBuilder res = new StringBuilder();
        int level = 0;
        for (Map.Entry<String, Long> time : times.entrySet()) {
            long timeDelta = duration / time.getValue();
            if (timeDelta > 0) {
                res.append(timeDelta)
                        .append(" ")
                        .append(time.getKey())
                        .append(timeDelta > 1 ? "s" : "")
                        .append(", ");
                duration -= time.getValue() * timeDelta;
                level++;
            }
            if (level == maxLevel) {
                break;
            }
        }
        if ("".equals(res.toString())) {
            return "0 seconds ago";
        } else {
            res.setLength(res.length() - 2);
            res.append(" ago");
            return res.toString();
        }
    }

    public static String toRelativeAbb(long duration, int maxLevel) {
        StringBuilder res = new StringBuilder();
        int level = 0;
        for (Map.Entry<String, Long> time : timesAbb.entrySet()) {
            long timeDelta = duration / time.getValue();
            if (timeDelta > 0) {
                res.append(timeDelta)
                        .append("")
                        .append(time.getKey())
                        .append(timeDelta > 1 ? "" : "")
                        .append(", ");
                duration -= time.getValue() * timeDelta;
                level++;
            }
            if (level == maxLevel) {
                break;
            }
        }
        if ("".equals(res.toString())) {
            return "0s ago";
        } else {
            res.setLength(res.length() - 2);
            res.append(" ago");
            return res.toString();
        }
    }

    public static String toRelative(long duration, boolean abb) {
        if (!abb)
            return toRelative(duration, times.size());
        return toRelativeAbb(duration, timesAbb.size());
    }

    public static String toRelative(Date start, Date end, boolean abb) {
        assert start.after(end);
        return toRelative(end.getTime() - start.getTime(), abb);
    }

    public static String toRelative(Date start, Date end, int level, boolean abb) {
        assert start.after(end);
        if (!abb)
            return toRelative(end.getTime() - start.getTime(), level);
        return toRelativeAbb(end.getTime() - start.getTime(), level);
    }
}
