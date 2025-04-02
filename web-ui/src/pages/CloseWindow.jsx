import React from 'react';
import { useEffect } from 'react';

export default function CloseWindow() {
  useEffect(() => {
    window.close();
  }, []);
  return <></>;
}
