import React from 'react';
import './Header.css';
import Avatar from '../avatar/Avatar';

const Header = ({title}) => {
    return (
    <div class='header'><div className="checkin">Check-in!</div><h1>{title}</h1><Avatar/></div>
    )
};

export default Header;