import React from 'react';
import {
    Link
} from "react-router-dom";
import './Menu.css';

const Menu = () => {

    return (
        <div>
            <input type="checkbox" id="menu-toggle" />
            <label id="trigger" for="menu-toggle"></label>
            <label id="burger" for="menu-toggle"></label>
            <ul id="menu">
                <li><Link to="/">Home</Link></li>
                <li><Link to="/team">My Team</Link></li>
                <li><Link to="/resources">Resources</Link></li>
                <li><Link to="/upload">Uploads</Link></li>
            </ul>
        </div >
    )
};

export default Menu;