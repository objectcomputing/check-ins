import React, { useRef, useEffect, useState } from 'react';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import './DatePickerField.css';

export default function DatePickerField({
  date,
  setDate,
  label,
  disabled,
  open
}) {
  const [cleared, setCleared] = useState(false);
  const launchDatePickerRef = useRef(null);

  useEffect(() => {
    if (cleared) {
      requestAnimationFrame(() => {
        setCleared(false);
      });
    }
  }, [cleared]);

  // This opens the DatePicker for the launch date if the open prop is set to true.
  useEffect(() => {
    const { current } = launchDatePickerRef;
    if (current && open) {
      const button = current.querySelector('button');
      button?.click();
    }
  }, [launchDatePickerRef.current]);

  return (
    <div className="datePickerField">
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <DatePicker
          label={label}
          value={date}
          format="YYYY-MM-DD"
          onChange={setDate}
          slotProps={{
            field: { clearable: true, onClear: () => setCleared(true) }
          }}
          disabled={disabled}
          ref={launchDatePickerRef}
        />
      </LocalizationProvider>
    </div>
  );
}
