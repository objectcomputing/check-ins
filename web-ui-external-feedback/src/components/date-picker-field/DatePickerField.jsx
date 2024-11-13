import dayjs from 'dayjs';
import React, { useRef, useEffect, useState } from 'react';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
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
    <div className="date-picker-field">
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <DatePicker
          disabled={disabled}
          format="YYYY-MM-DD"
          label={label}
          onChange={setDate}
          ref={launchDatePickerRef}
          slotProps={{
            field: { clearable: true, onClear: () => setCleared(true) }
          }}
          value={date ? dayjs(date) : null}
        />
      </LocalizationProvider>
    </div>
  );
}
