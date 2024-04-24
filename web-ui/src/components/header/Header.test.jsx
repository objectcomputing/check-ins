import React from 'react';
import Header from './Header';
import renderer from 'react-test-renderer';

it('renders title', () => {
  snapshot(<Header title="Ze title" />);
});
