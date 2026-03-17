export interface ActivitySummary {
  id: string;
  name: string;
  points: number;
  category?: string;
  timestamp?: string;
  impact?: string;
}

export const mockActivities: ActivitySummary[] = [
  {
    id: '1',
    name: 'Mit dem Rad zur Schule',
    points: 24,
    category: 'MOBILITAET',
    timestamp: 'Heute, 07:35',
    impact: '2,1 kg CO2 gespart',
  },
  {
    id: '2',
    name: 'Mehrwegflasche verwendet',
    points: 8,
    category: 'RECYCLING',
    timestamp: 'Gestern, 12:10',
    impact: '1 Plastikflasche vermieden',
  },
  {
    id: '3',
    name: 'Licht im Klassenraum ausgeschaltet',
    points: 12,
    category: 'ENERGIE',
    timestamp: 'Gestern, 15:48',
    impact: '0,7 kWh eingespart',
  },
  {
    id: '4',
    name: 'Vegetarisches Mittagessen',
    points: 16,
    category: 'ERNAEHRUNG',
    timestamp: 'Montag, 11:55',
    impact: '1,6 kg CO2 vermieden',
  },
];
