const REDUCED_MOTION_QUERY = '(prefers-reduced-motion: reduce)';

export function prefersReducedMotion(): boolean {
  return matchMedia(REDUCED_MOTION_QUERY).matches;
}
