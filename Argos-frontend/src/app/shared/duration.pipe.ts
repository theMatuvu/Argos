import { Pipe, PipeTransform } from '@angular/core';

const MINUTES_PER_HOUR = 60;
const HOURS_PER_DAY = 24;
const MINUTES_PER_DAY = MINUTES_PER_HOUR * HOURS_PER_DAY;

@Pipe({ name: 'duration' })
export class DurationPipe implements PipeTransform {
  transform(totalMinutes: number): string {
    const days = Math.floor(totalMinutes / MINUTES_PER_DAY);
    const hours = Math.floor((totalMinutes % MINUTES_PER_DAY) / MINUTES_PER_HOUR);
    const minutes = totalMinutes % MINUTES_PER_HOUR;
    if (days > 0) {
      return `${days} j ${hours} h`;
    }
    if (hours > 0) {
      return `${hours} h ${minutes} min`;
    }
    return minutes > 0 ? `${minutes} min` : "moins d'une minute";
  }
}
