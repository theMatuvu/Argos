const SEARCH_LOCALE = 'fr';

const normalize = (text: string): string =>
  text
    .normalize('NFD')
    .replace(/\p{Diacritic}/gu, '')
    .toLocaleLowerCase(SEARCH_LOCALE);

export function matchesSearch(query: string, fields: (string | null | undefined)[]): boolean {
  const normalizedQuery = normalize(query.trim());
  return (
    !normalizedQuery ||
    fields.some((field) => field != null && normalize(field).includes(normalizedQuery))
  );
}
