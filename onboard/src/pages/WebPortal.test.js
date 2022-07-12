import React from "react";
import WebPortal from "./WebPortal";
import {render,screen} from '@testing-library/react';

describe('Sidebar component', () => {
    test('Render OCI logo image', () => {
        render(<WebPortal />);
        const ociLogo = screen.getByAltText('Object Computing, Inc.');
        expect(ociLogo).toBeInTheDocument();
    });
    
    test('renders culture video menu item', () => {
        render(<WebPortal/>);
        const cultureVideo = screen.getByText('1) Culture Video');
        expect(cultureVideo).toBeInTheDocument();
    }); 

    test('renders survey menu item', () => {
        render(<WebPortal/>);
        const aboutYouSurvey = screen.getByText('2) About You Survey');
        expect(aboutYouSurvey).toBeInTheDocument();
    }); 

    test('renders work preference item', () => {
        render(<WebPortal/>);
        const workPreference = screen.getByText('3) Work Preference');
        expect(workPreference).toBeInTheDocument();
    }); 

    test('renders computer and accessories item', () => {
        render(<WebPortal/>);
        const computerAccessory = screen.getByText('4) Computer and Accessories');
        expect(computerAccessory).toBeInTheDocument();
    }); 

    test('renders internal document signing item', () => {
        render(<WebPortal/>);
        const docuSign = screen.getByText('5) Internal Document Signing');
        expect(docuSign).toBeInTheDocument();
    }); 

    test('renders check-ins skills item', () => {
        render(<WebPortal/>);
        const checkIn = screen.getByText('6) Check-Ins Skills');
        expect(checkIn).toBeInTheDocument();
    }); 

    test('renders cake item', () => {
        render(<WebPortal/>);
        const cake = screen.getByText('7) Cake!');
        expect(cake).toBeInTheDocument();
    }); 
});