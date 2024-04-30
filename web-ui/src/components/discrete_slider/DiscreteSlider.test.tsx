import React from 'react';
// @ts-ignore
import DiscreteSlider from './DiscreteSlider';

vi.mock('@mui/material/Slider', () => {
  return {
    default: () => (props: any) => {
      const { onChange, 'data-testid': testId, ...rest } = props;

      return (
        <input
          data-testid={testId}
          type="range"
          onChange={event => {
            onChange(null, parseInt(event.target.value, 10));
          }}
          {...rest}
        />
      );
    }
  };
});

describe('DiscreteSlider', () => {
  it('renders slider with title', () => {
    // @ts-ignore
    snapshot(<DiscreteSlider title="Some skill" />, {
      createNodeMock: (element: any) => {
        if (element.type === 'div') {
          return {
            addEventListener: vi.fn()
          };
        }
      }
    });
  });
});
