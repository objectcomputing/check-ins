import React from 'react';
import ociLogo from './img/ocicube-white.png';
import styled from 'styled-components';

const IMG = styled.img`
  width: 100%;
  background-image: url(${ociLogo});
  background-position: left;
  background-repeat: no-repeat;
  background-size: contain;
  border: 1px #0095ff solid;
  border-radius: 50%;
`;

const LogoDisplay = ({ width, height }) => {
  let maxWidth = width ? width : '100%';
  let fullHeight = height ? height : '100%';

  return <IMG style={{ maxWidth: maxWidth, height: fullHeight }} />;
};

export default LogoDisplay;
