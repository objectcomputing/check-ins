import React from "react";
import EquipmentPage from "./EquipmentPage";
import {render,screen} from '@testing-library/react';

describe("Equipment page", () => {
    test("Render header text", () => {
        render(<EquipmentPage />);
        const headerText = screen.getByText("Please select your computer preference:");
        expect(headerText).toBeInTheDocument();
    });

    test("Buttons render", () => {
        render(<EquipmentPage />);
        const equipBtns = screen.getAllByRole('button');
        expect(equipBtns.length).toBe(13);
    });

    test("Input field render", () => {
        render(<EquipmentPage />);
        const inputField = screen.getByRole('textbox');
        expect(inputField).toBeInTheDocument();
    });
});