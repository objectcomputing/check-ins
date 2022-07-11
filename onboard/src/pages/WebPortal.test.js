import React from "react";
import WebPortal from "./WebPortal";
import {render,screen} from '@testing-library/react';

describe('Sidebar component', () => {
    test('renders culture video menu item', () => {
        render(<WebPortal/>);
        const cultureVideo = screen.getByText('Culture Video');
        expect(cultureVideo).toBeInTheDocument();
    }); 

    test('renders survey menu item', () => {
        render(<WebPortal/>);
        const aboutYouSurvey = screen.getByText('About You Survey');
        expect(aboutYouSurvey).toBeInTheDocument();
    }); 

    test('renders work preference item', () => {
        render(<WebPortal/>);
        const workPreference = screen.getByText('Work Preference');
        expect(workPreference).toBeInTheDocument();
    }); 

    test('renders computer and accessories item', () => {
        render(<WebPortal/>);
        const computerAccessory = screen.getByText('Computer and Accessories');
        expect(computerAccessory).toBeInTheDocument();
    }); 

    test('renders internal document signing item', () => {
        render(<WebPortal/>);
        const docuSign = screen.getByText('Internal Document Signing');
        expect(docuSign).toBeInTheDocument();
    }); 

    test('renders check-ins skills item', () => {
        render(<WebPortal/>);
        const checkIn = screen.getByText('Check-Ins Skills');
        expect(checkIn).toBeInTheDocument();
    }); 

    test('renders cake item', () => {
        render(<WebPortal/>);
        const cake = screen.getByText('Cake!');
        expect(cake).toBeInTheDocument();
    }); 
});