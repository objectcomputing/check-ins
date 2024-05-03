import { render, screen } from '@testing-library/react';
import ExpandMore from './ExpandMore';

describe('ExpandMore', () => {
  it('should render the component', () => {
    render(<ExpandMore />);

    const button = screen.getByRole('button');
    expect(button).toBeInTheDocument();

    // is collapsed by default
    expect(button).toHaveStyle('transform: rotate(0deg)');
  });

  it('should rotate the icon when expanded', () => {
    render(<ExpandMore expand />);

    const button = screen.getByRole('button');
    expect(button).toHaveStyle('transform: rotate(180deg)');
  });

  it('should rotate the icon when collapsed', () => {
    render(<ExpandMore expand={false} />);

    const button = screen.getByRole('button');
    expect(button).toHaveStyle('transform: rotate(0deg)');
  });

  it('spreads its props to the button', () => {
    render(<ExpandMore data-testid="expand-more" />);

    const button = screen.getByTestId('expand-more');
    expect(button).toBeInTheDocument();
  });

  it('spread props include aria-label and aria-expanded', () => {
    render(<ExpandMore aria-label="expand" aria-expanded={false} />);

    const button = screen.getByRole('button');
    expect(button).toHaveAttribute('aria-label', 'expand');
    expect(button).toHaveAttribute('aria-expanded', 'false');
  });

  it('spread props include id and className', () => {
    render(<ExpandMore id="expand-more" className="expand-more" />);

    const button = screen.getByRole('button');
    expect(button).toHaveAttribute('id', 'expand-more');
    expect(button).toHaveClass('expand-more');
  });

  it('displays the expand more icon when no children are provided', () => {
    render(<ExpandMore />);

    const button = screen.getByRole('button');
    const icon = button.querySelector('svg');
    expect(icon).toHaveAttribute('data-testid', 'ExpandMoreIcon');
  });

  it('displays the children when provided', () => {
    render(<ExpandMore>Test</ExpandMore>);

    const button = screen.getByRole('button');
    expect(button).toHaveTextContent('Test');
  });
});
