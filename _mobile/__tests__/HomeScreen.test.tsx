import { fireEvent, render } from '@testing-library/react-native';
import React from 'react';
import { HomeScreen } from '../src/screens/home/home-screen';

const mockNavigate = jest.fn();

jest.mock('@react-navigation/native', () => ({
  useNavigation: () => ({
    navigate: mockNavigate,
  }),
}), { virtual: true });

describe('HomeScreen', () => {
  beforeEach(() => {
    mockNavigate.mockClear();
  });

  it('should_navigateToActivityDetail_when_activityPressed', () => {
    const { getAllByTestId } = render(<HomeScreen />);

    fireEvent.press(getAllByTestId('activity-item')[0]);

    expect(mockNavigate).toHaveBeenCalledWith('ActivityDetail', { activityId: '1' });
  });

  it('should_matchSnapshot_when_rendered', () => {
    const { toJSON } = render(<HomeScreen />);

    expect(toJSON()).toMatchSnapshot();
  });
});
