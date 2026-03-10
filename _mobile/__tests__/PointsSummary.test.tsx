import { render } from '@testing-library/react-native';
import React from 'react';
import { PointsSummary } from '../src/components/features/PointsSummary';

describe('PointsSummary', () => {
  it('should_renderPointsValue_when_componentMounted', () => {
    const { getByText } = render(<PointsSummary userId="demo-user" />);

    expect(getByText('Deine Punkte')).toBeTruthy();
    expect(getByText('120')).toBeTruthy();
  });

  it('should_matchSnapshot_when_rendered', () => {
    const { toJSON } = render(<PointsSummary userId="demo-user" />);

    expect(toJSON()).toMatchSnapshot();
  });
});
