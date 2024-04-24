import React from 'react';
import SplitButton from './SplitButton';
import renderer from 'react-test-renderer';

it('renders correctly', () => {
  snapshot(<SplitButton options={['One', 'Two', 'Three']} onClick={vi.fn()} />);
});
