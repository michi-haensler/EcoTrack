import { render } from '@testing-library/react-native';
import React from 'react';
import { PointsSummary } from '../src/components/features/PointsSummary';

describe('PointsSummary', () => {
  it('should_renderPointsValue_when_componentMounted', () => {
    const { getAllByText, getByText } = render(<PointsSummary userId="demo-user" />);

    expect(getByText('Deine Punkte')).toBeTruthy();
    expect(getAllByText('0').length).toBeGreaterThanOrEqual(2);
  });

  it('should_renderLevelInformation_when_rendered', () => {
    const { getAllByText, getByText } = render(<PointsSummary userId="demo-user" />);

    expect(getAllByText('Setzling').length).toBeGreaterThanOrEqual(1);
    expect(getByText('Aktivitaeten')).toBeTruthy();
  });
});
