import React from 'react';
import {fireEvent, render} from '@testing-library/react';
import ActionItemsPanel from './ActionItemsPanel';

global.requestAnimationFrame = function (callback) {
  setTimeout(callback, 0);
};

function dumpElements(root, depth = 1) {
  const keys = Object.keys(root);
  const element = root[keys[0]];
  console.log(depth, 'type', element.type, element.textContent);
  //if (element.type === 'p') console.log(element);
  for (const child of root.children) {
    dumpElements(child, depth + 1);
  }
}

it('renders correctly', () => {
  snapshot(<ActionItemsPanel />);
});

it('handles drag and drop', () => {
  const actionItems = [
    {id: 'a1', description: 'first action item'},
    {id: 'a2', description: 'second action item'},
    {id: 'a3', description: 'third action item'}
  ];
  const {container} = render(
    <ActionItemsPanel mockActionItems={actionItems} />
  );

  //TODO: Is this really necessary?
  const createBubbledEvent = (type, props = {}) => {
    const event = new Event(type, {bubbles: true});
    Object.assign(event, props);
    return event;
  };

  let aic = container.querySelector('.action-items-container');
  expect(aic).not.toBeNull();

  let droppable = aic.querySelector(':scope > div');
  expect(droppable).not.toBeNull();
  //dumpElements(droppable);

  let draggables = droppable.children;

  // Verify that there are at least two action items.
  expect(draggables.length > 1).toBe(true);

  // Get the DOM element for the first and second action items.
  let [firstActionItem, secondActionItem] = draggables;
  // Get the text in the first and second action items.
  const firstText = firstActionItem.querySelector('p').textContent;
  const secondText = secondActionItem.querySelector('p').textContent;

  // Get the DOM element for the drag handle of the first action item.
  const dragHandle = firstActionItem.querySelector('span');

  // Get the center x and center y of the first drag handle.
  // We can't do this because the getBoundingClientRect method
  // just returns zeroes in jsdom.
  /*
  const box1 = dragHandle.getBoundingClientRect();
  const dragHandleX = box1.x + box1.width / 2;
  const dragHandleY = box1.y + box1.height / 2;
  */
  const dragHandleX = 0;
  const dragHandleY = 0;

  // Get the bottom y of the second action item.
  // We can't do this because the getBoundingClientRect method
  // just returns zeroes in jsdom.
  /*
  const box2 = secondActionItem.getBoundingClientRect();
  const secondItemBottomY = box2.y + box2.height;
  */
  const secondItemBottomY = 0;

  dragHandle.dispatchEvent(
    createBubbledEvent('dragstart', {
      clientX: dragHandleX,
      clientY: dragHandleY
    })
  );
  droppable.dispatchEvent(
    createBubbledEvent('drop', {clientX: 0, clientY: secondItemBottomY})
  );

  // Get all the DOM elements for the action items.
  aic = container.querySelector('.action-items-container');
  droppable = aic.querySelector(':scope > div');
  draggables = droppable.children;
  [firstActionItem, secondActionItem] = draggables;

  // Verify that the DOM element for what was
  // the first action item is now the second.
  const newFirstText = firstActionItem.querySelector('p').textContent;
  const newSecondText = secondActionItem.querySelector('p').textContent;
  expect(newFirstText).toBe(actionItems[0].description);
  expect(newSecondText).toBe(actionItems[1].description);
});
