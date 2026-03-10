import { render } from '@testing-library/react-native';
import React from 'react';
import { ActivityList } from '../src/components/features/ActivityList';

describe('ActivityList', () => {
  it('should_renderActivityItems_when_defaultState', () => {
    const { getByText } = render(<ActivityList userId="demo-user" />);

    expect(getByText('Aktivitaeten')).toBeTruthy();
    expect(getByText('Radfahren')).toBeTruthy();
    expect(getByText('Laufen')).toBeTruthy();
  });

  it('should_renderLoadingState_when_isLoadingTrue', () => {
    const { getByText } = render(<ActivityList userId="demo-user" isLoading={true} />);

    expect(getByText('Aktivitaeten werden geladen...')).toBeTruthy();
  });

  it('should_renderErrorState_when_errorProvided', () => {
    const { getByText } = render(
      <ActivityList userId="demo-user" error="Fehler beim Laden" />
    );

    expect(getByText('Fehler beim Laden')).toBeTruthy();
  });

  it('should_matchSnapshot_when_rendered', () => {
    const { toJSON } = render(<ActivityList userId="demo-user" />);

    expect(toJSON()).toMatchSnapshot();
  });
});
