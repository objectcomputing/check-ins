import React from "react";
import {fireEvent, render, screen, waitFor} from "@testing-library/react";
import ActionItemsPanel from "./ActionItemsPanel";
import { AppContextProvider } from "../../context/AppContext";
import { rest } from "msw";
import { setupServer } from "msw/node";

window.snackDispatch = jest.fn();

const actionItems = [
  { id: "a1", description: "first action item" },
  { id: "a2", description: "second action item" },
  { id: "a3", description: "third action item" },
];

const server = setupServer(
    rest.get('http://localhost:8080/services/member-profile/current', (req, res, ctx) => {
      return res(ctx.json({ id: "12345", name: "Test User" }));
    }),
    rest.get('http://localhost:8080/services/team/member', (req, res, ctx) => {
      return res(ctx.json([{ id: "12345", name: "Test User" }]));
    }),
    rest.get('http://localhost:8080/services/action-item?checkinid=394810298371&createdbyid=912834091823', (req, res, ctx) => {
      return res(ctx.json(actionItems));
    })
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

global.requestAnimationFrame = function (callback) {
  setTimeout(callback, 0);
};

function dumpElements(root, depth = 1) {
  const keys = Object.keys(root);
  const element = root[keys[0]];
  console.log(depth, "type", element.type, element.textContent);
  //if (element.type === 'p') console.log(element);
  for (const child of root.children) {
    dumpElements(child, depth + 1);
  }
}

const initialState = {
  state: {
    userProfile: {
      memberProfile: {
        id: "912834091823",
      },
    },
  },
};

it("renders correctly", () => {
  snapshot(
    <AppContextProvider value={initialState}>
      <ActionItemsPanel checkinId="394810298371" memberName="mr. test" />
    </AppContextProvider>
  );
});

it("handles drag and drop", async () => {
  const { container } = render(
    <AppContextProvider value={initialState}>
      <ActionItemsPanel />
    </AppContextProvider>
  );

  await waitFor(() => screen.getByDisplayValue(/first action item/i));

  //TODO: Is this really necessary?
  const createBubbledEvent = (type, props = {}) => {
    const event = new Event(type, { bubbles: true });
    Object.assign(event, props);
    return event;
  };

  let aic = container.querySelector(".action-items-container");
  expect(aic).not.toBeNull();

  let droppable = aic.querySelector(":scope > div");
  expect(droppable).not.toBeNull();
  //dumpElements(droppable);

  let draggables = droppable.children;

  // Verify that there are at least two action items.
  expect(draggables.length > 1).toBe(true);

  // Get the DOM element for the first and second action items.
  let [firstActionItem, secondActionItem] = draggables;

  console.log("DRAGGABLES => "+draggables.length);

  // Get the text in the first and second action items.
  const firstText = firstActionItem.querySelector("input").value;
  const secondText = secondActionItem.querySelector("input").value;

  // Get the DOM element for the drag handle of the first action item.
  const dragHandle = firstActionItem.querySelector("span");

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
    createBubbledEvent("dragstart", {
      clientX: dragHandleX,
      clientY: dragHandleY,
    })
  );
  droppable.dispatchEvent(
    createBubbledEvent("drop", { clientX: 0, clientY: secondItemBottomY })
  );

  // Get all the DOM elements for the action items.
  aic = container.querySelector(".action-items-container");
  droppable = aic.querySelector(":scope > div");
  draggables = droppable.children;
  [firstActionItem, secondActionItem] = draggables;

  // Verify that the DOM element for what was
  // the first action item is now the second.
  const newFirstText = firstActionItem.querySelector("input").value;
  const newSecondText = secondActionItem.querySelector("input").value;
  expect(newFirstText).toBe(actionItems[0].description);
  expect(newSecondText).toBe(actionItems[1].description);
});
