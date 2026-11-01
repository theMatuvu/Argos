export interface NetworkStation {
  id: string;
  name: string;
  latitude: number;
  longitude: number;
}

export interface LineSegment {
  fromStationId: string;
  toStationId: string;
}

export interface NetworkLine {
  id: string;
  name: string;
  color: string;
  textColor: string;
  segments: LineSegment[];
}

export interface Network {
  stations: NetworkStation[];
  lines: NetworkLine[];
}
