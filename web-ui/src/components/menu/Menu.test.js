import React from "react";
import renderer from "react-test-renderer";
import Menu from "./Menu";
import { MemoryRouter } from "react-router-dom";
import { AppContextProvider } from "../../context/AppContext";
import EnzymeToJson from "enzyme-to-json";
import { mount } from "enzyme";

const initialState = {
  state: {
    userProfile: {
      name: "holmes",
      memberProfile: {
        pdlId: "",
        title: "Tester",
        workEmail: "test@tester.com",
      },
      role: ["MEMBER"],
      imageUrl:
        "https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg",
    },
  },
};

describe("<Menu />", () => {
  it("renders correctly", () => {
    const component = mount(
      <>
        <AppContextProvider value={initialState}>
          <MemoryRouter initialEntries={["/guilds"]}>
            <Menu />
          </MemoryRouter>
        </AppContextProvider>
      </>
    );
    expect(EnzymeToJson(component)).toMatchSnapshot();
  });
});
