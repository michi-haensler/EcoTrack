import { fireEvent, render } from '@testing-library/react-native';
import React from 'react';
import { ActivityItem } from '../src/components/features/ActivityItem';

describe('ActivityItem', () => {
  it('should_callOnPressWithId_when_itemPressed', () => {
    const onPress = jest.fn();
    const activity = { id: '1', name: 'Radfahren', points: 10 };

    const { getByTestId } = render(
      <ActivityItem activity={activity} onPress={onPress} />
    );

    fireEvent.press(getByTestId('activity-item'));

    expect(onPress).toHaveBeenCalledWith('1');
  });

  it('should_matchSnapshot_when_rendered', () => {
    const activity = { id: '1', name: 'Radfahren', points: 10 };
    const { toJSON } = render(<ActivityItem activity={activity} />);

    expect(toJSON()).toMatchSnapshot();
  });
});
