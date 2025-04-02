import React, { useCallback, useContext, useEffect, useState } from 'react';
import { UPDATE_TOAST } from '../context/actions';
import { AppContext } from '../context/AppContext';
import { Button, Typography } from '@mui/material';
import {
  SettingsBoolean,
  SettingsColor,
  SettingsFile,
  SettingsNumber,
  SettingsString
} from '../components/settings';
import { putOption, postOption, getAllOptions } from '../api/settings';
import {
  selectCsrfToken,
  selectHasViewSettingsPermission,
  selectHasAdministerSettingsPermission,
  noPermission
} from '../context/selectors';
import { titleCase } from '../helpers/strings';
import './SettingsPage.css';

const displayName = 'SettingsPage';

const componentMapping = {
  BOOLEAN: SettingsBoolean,
  COLOR: SettingsColor,
  FILE: SettingsFile,
  NUMBER: SettingsNumber,
  STRING: SettingsString
};

const constructFileHandler = setting => {
  return file => {
    console.log(
      `Pretend we saved that file for ${setting.name} somewhere (we didn't)...`
    );
  };
};

const SettingsPage = () => {
  const fileRef = React.useRef(null);
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const [settings, setSettings] = useState([]);
  const [handlers, setHandlers] = useState({});
  const [update, setUpdate] = useState(true);
  const canView =
    selectHasViewSettingsPermission(state) ||
    selectHasAdministerSettingsPermission(state);

  useEffect(() => {
    const fetchData = async () => {
      // Get the options from the server
      const allOptions = canView
        ? (await getAllOptions(csrf)).payload.data
        : [];

      if (allOptions?.length !== 0) {
        // Sort the options by category, store them, and update the state.
        const sorted = allOptions.sort((l, r) => {
          if (l.category === r.category) {
            return l.name.localeCompare(r.name);
          } else {
            return l.category.localeCompare(r.category);
          }
        });

        setSettings(sorted);
        setUpdate(false);
      }
    };
    if (csrf && update) {
      fetchData();
    }
  }, [csrf, canView, update, setUpdate, setSettings]);

  useEffect(() => {
    if (settings?.length !== 0) {
      setHandlers(
        settings.reduce((acc, curr) => {
          if (curr.type.toUpperCase() === 'FILE') {
            acc[curr.name] = constructFileHandler(curr);
          } else {
            acc[curr.name] = event => {
              const newSettings = [...settings];
              const setting = newSettings.find(test => test.name === curr.name);
              setting.value = event.target.value;
              setSettings(newSettings);
            };
          }
          return acc;
        }, {})
      );
    }
  }, [setHandlers, setSettings, settings]);

  const save = useCallback(async () => {
    if (settings && settings.length > 0) {
      let errors;
      let promises = settings
        .filter(setting => setting.type.toUpperCase() !== 'FILE')
        .map(setting => {
          let promise = setting.id
            ? putOption({ name: setting.name, value: setting.value }, csrf)
            : postOption({ name: setting.name, value: setting.value }, csrf);

          return promise;
        });

      Promise.all(promises).then(results => {
        results.forEach(res => {
          if (res?.error) {
            const error = res?.error?.message;
            if (errors) {
              errors += '\n' + error;
            } else {
              errors = error;
            }
          }
        });

        // Trigger load of updated settings values and display errors or success.
        setUpdate(true);
        if (errors) {
          dispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: 'error',
              toast: errors
            }
          });
        } else {
          dispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: 'success',
              toast: 'Settings have been saved'
            }
          });
        }
      });
    }
  }, [settings]);

  const categories = {};

  return canView ? (
    <div className="settings-page">
      {settings.map((setting, index) => {
        const Component = componentMapping[setting.type.toUpperCase()];
        const info = { ...setting, name: titleCase(setting.name) };
        setting.type === 'FILE'
          ? (info.handleFunction = handlers[setting.name])
          : (info.handleChange = handlers[setting.name]);
        if (categories[info.category]) {
          return <Component key={index} {...info} />;
        } else {
          categories[info.category] = true;
          return (
            <div key={index}>
              <Typography
                data-testid={info.category}
                variant="h4"
                sx={{ textDecoration: 'underline' }}
                display="inline"
              >
                {titleCase(info.category)}
              </Typography>
              <Component {...info} />
            </div>
          );
        }
      })}
      {
        // Check length against an explicit value.  If length is zero, it will
        // be displayed instead of evaluated to false.
        settings &&
          settings.length > 0 &&
          selectHasAdministerSettingsPermission(state) && (
            <div className="buttons">
              <Button disableRipple color="primary" onClick={save}>
                Save
              </Button>
            </div>
          )
      }
    </div>
  ) : (
    <h3>{noPermission}</h3>
  );
};

SettingsPage.displayName = displayName;

export default SettingsPage;
