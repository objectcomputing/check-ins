import React from 'react';
import ProgressBar from './ProgressBar';
import renderer from 'react-test-renderer';

it('has billable hours', () => {
  snapshot(<ProgressBar props={(1200, 1500, 1850, 0, 0, 0)} />);
});

it('has no billable hours', () => {
  snapshot(<ProgressBar props={(0, 1500, 1850, 0, 0, 0)} />);
});
