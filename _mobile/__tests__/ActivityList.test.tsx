import { render } from '@testing-library/react-native';
import React from 'react';
import { ActivityList } from '../src/components/features/ActivityList';
import { mockActivities } from '../src/components/features/activity-types';

describe('ActivityList', () => {
  it('should_renderActivityItems_when_defaultState', () => {
    const { getByText } = render(<ActivityList userId="demo-user" />);

    expect(getByText('Letzte Aktionen')).toBeTruthy();
    expect(getByText(mockActivities[0].name)).toBeTruthy();
    expect(getByText(mockActivities[1].name)).toBeTruthy();
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

  it('should_renderSubtitle_when_rendered', () => {
    const { getByText } = render(<ActivityList userId="demo-user" />);

    expect(getByText('Deine juengsten nachhaltigen Entscheidungen im Ueberblick.')).toBeTruthy();
  });
});
