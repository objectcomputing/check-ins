import React from "react";
import { render } from "@testing-library/react";
import ActionItemsPanel from "./ActionItemsPanel";
import { AppContextProvider } from "../../context/AppContext";

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

// we tried making it work according to the example here but wasted too much time
// https://www.freecodecamp.org/news/how-to-write-better-tests-for-drag-and-drop-operations-in-the-browser-f9a131f0b281/
it.skip("handles drag and drop", () => {
  const checkinid = "a1";
  const createdbyid = "a2";
  const actionItems = [
    {
      checkinid,
      createdbyid,
      description: "stuff",
      id: "f2d929a4-887d-43d8-82b1-decf4bb64926",
      priority: 1.5,
    },
    {
      checkinid,
      createdbyid,
      description: "description",
      id: "887d-43d8-82b1-decf4bb64926",
      priority: 2.5,
    },
  ];
  const { container } = render(
    <AppContextProvider value={initialState}>
      <ActionItemsPanel memberName="ms. test" mockActionItems={actionItems} />
    </AppContextProvider>
  );

  const createBubbledEvent = (type, props = {}) => {
    const event = new Event(type, { bubbles: true });
    Object.assign(event, props);
    return event;
  };

  let aic = container.querySelector(".action-items-container");
  expect(aic).not.toBeNull();
  let droppable = aic.firstChild;
  let firstChild = droppable.firstChild;
  let secondChild = firstChild.nextSibling;

  expect(firstChild).not.toBeNull();
  expect(secondChild).not.toBeNull();

  const firstText = firstChild.querySelector(".text-input").value;
  const secondText = secondChild.querySelector(".text-input").value;

  const dragHandle1 = firstChild.querySelector("span");

  dragHandle1.dispatchEvent(
    createBubbledEvent("dragstart", {
      clientX: 0,
      clientY: 0,
    })
  );
  droppable.dispatchEvent(
    createBubbledEvent("drop", { clientX: 0, clientY: 73 })
  );
  firstChild = droppable.firstChild;
  secondChild = firstChild.nextSibling;

  const newFirstText = firstChild.querySelector(".text-input").value;
  const newSecondText = secondChild.querySelector(".text-input").value;

  expect(newFirstText).toBe(secondText);
  expect(newSecondText).toBe(firstText);
});
