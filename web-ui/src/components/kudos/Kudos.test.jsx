import React from 'react';
import renderer from 'react-test-renderer';
import Kudos from './Kudos';
import image from '../../logo.svg';

const kudosData = {
  kudosTo: {
    name: 'John Doe',
    imageUrl: image
  },
  content:
    'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
  kudosFrom: {
    name: 'Jane Doe',
    imageUrl: image,
    title: 'Senior Software Engineer'
  }
};

it("renders correctly when 'kudosFrom' prop is provided", () => {
  const component = renderer.create(
    <Kudos
      kudosTo={kudosData.kudosTo}
      content={kudosData.content}
      kudosFrom={kudosData.kudosFrom}
    />
  );
  expect(component.toJSON()).toMatchSnapshot();
});

it("renders correctly when 'kudosFrom' prop is not provided", () => {
  const component = renderer.create(
    <Kudos kudosTo={kudosData.kudosTo} content={kudosData.content} />
  );
  expect(component.toJSON()).toMatchSnapshot();
});
