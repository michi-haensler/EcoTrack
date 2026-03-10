import { fireEvent, render } from '@testing-library/react-native';
import React from 'react';
import { ActivitiesScreen } from '../src/screens/activities/activities-screen';

const mockNavigate = jest.fn();

jest.mock('@react-navigation/native', () => ({
  useNavigation: () => ({
    navigate: mockNavigate,
  }),
}), { virtual: true });

describe('ActivitiesScreen', () => {
  beforeEach(() => {
    mockNavigate.mockClear();
  });

  it('should_navigateToActivityDetail_when_activityPressed', () => {
    const { getAllByTestId } = render(<ActivitiesScreen />);

    fireEvent.press(getAllByTestId('activity-item')[0]);

    expect(mockNavigate).toHaveBeenCalledWith('ActivityDetail', { activityId: '1' });
  });

  it('should_renderRefreshControl_when_screenMounted', () => {
    const { getByTestId } = render(<ActivitiesScreen />);

    expect(getByTestId('activities-list').props.refreshControl).toBeTruthy();
  });
});
