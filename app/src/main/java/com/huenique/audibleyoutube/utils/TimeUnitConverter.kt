package com.huenique.audibleyoutube.utils

import java.util.concurrent.TimeUnit

class TimeUnitConverter {
  fun milliToMinSec(milliseconds: Long): String {
    return String.format(
        "%02d : %02d",
        TimeUnit.MILLISECONDS.toMinutes(milliseconds) -
            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)),
        TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)))
  }
}
