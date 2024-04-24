import React, { useContext, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import FileUploader from './FileUploader';
import { getFiles, deleteFile, uploadFile } from '../../../api/upload';
import { AppContext } from '../../../context/AppContext';
import { UPDATE_TOAST } from '../../../context/actions';
import {
  selectCsrfToken,
  selectCurrentUser,
  selectIsPDL,
  selectIsAdmin,
  selectCheckin
} from '../../../context/selectors';
import DescriptionIcon from '@mui/icons-material/Description';
import IconButton from '@mui/material/IconButton';
import DeleteIcon from '@mui/icons-material/Delete';
import { CircularProgress } from '@mui/material';
import Card from '@mui/material/Card';
import CardHeader from '@mui/material/CardHeader';
import CardContent from '@mui/material/CardContent';
import CardActions from '@mui/material/CardActions';

import './CheckinDocs.css';

const UploadDocs = () => {
  const { state, dispatch } = useContext(AppContext);
  const { checkinId, memberId } = useParams();
  const csrf = selectCsrfToken(state);
  const memberProfile = selectCurrentUser(state);
  const currentUserId = memberProfile?.id;
  const currentCheckin = selectCheckin(state, checkinId);

  const [loading, setLoading] = useState(false);
  const [files, setFiles] = useState([]);
  const [fileColors, setFileColors] = useState({});

  const pdlorAdmin = selectIsPDL(state) || selectIsAdmin(state);
  const canView = pdlorAdmin && currentUserId !== memberId;

  useEffect(() => {
    async function getCheckinFiles() {
      try {
        let res = await getFiles(checkinId, csrf);
        if (res.error) throw new Error(res.error);
        let checkinFiles =
          res.payload && res.payload.data && res.payload.data.length > 0
            ? res.payload.data
            : null;
        if (checkinFiles) {
          setFiles(checkinFiles);
          checkinFiles.forEach(file => {
            setFileColors(fileColors => ({
              ...fileColors,
              [file.name]: 'green'
            }));
          });
        }
      } catch (e) {
        console.log(e);
      }
    }
    if (csrf) {
      getCheckinFiles();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [checkinId, csrf]);

  const handleFile = file => {
    if (csrf) {
      setFiles([...files, file]);
      addFile(file);
    }
  };

  const addFile = async file => {
    let formData = new FormData();
    formData.append('file', file);
    if (!file || files.includes(file)) {
      setLoading(false);
      return;
    }
    setLoading(true);
    try {
      let res = await uploadFile(formData, checkinId, csrf);
      if (res.error) throw new Error(res.error);
      const { data, status } = res.payload;
      if (status !== 200 && status !== 201) {
        throw new Error('status equals ' + status);
      }
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'success',
          toast: `${data.name} was successfully uploaded`
        }
      });
      setFileColors(fileColors => ({ ...fileColors, [file.name]: 'green' }));
      setFiles([...files, data]);
    } catch (e) {
      setFileColors(fileColors => ({ ...fileColors, [file.name]: 'red' }));
      console.log({ e });
    } finally {
      setLoading(false);
    }
  };

  const fileMapper = () => {
    const divs = files.map(file => {
      if (!file.name) {
        return null;
      } else {
        let downloadUrl = '/services/files/' + file.fileId + '/download';
        return (
          <div key={file.fileId} style={{ color: fileColors[file.name] }}>
            <a href={downloadUrl} download={file.name}>
              {file.name}
            </a>
            <IconButton
              disabled={currentCheckin?.completed}
              onClick={async () => {
                if (csrf) {
                  await deleteFile(file.fileId, csrf);
                  setFiles(
                    files.filter(e => {
                      return e.name !== file.name;
                    })
                  );
                }
              }}
              size="large"
            >
              <DeleteIcon />
            </IconButton>
          </div>
        );
      }
    });
    return files.length > 0 ? divs : 'No files attached.';
  };

  const hiddenFileInput = React.useRef(null);

  return canView ? (
    <Card>
      <CardHeader
        avatar={<DescriptionIcon />}
        title="Documents"
        titleTypographyProps={{ variant: 'h5', component: 'h2' }}
      />
      <CardContent>
        <div className="file-name-container">{fileMapper()}</div>
      </CardContent>
      <CardActions>
        {loading ? (
          <CircularProgress />
        ) : (
          <FileUploader handleFile={handleFile} fileRef={hiddenFileInput} />
        )}
      </CardActions>
    </Card>
  ) : (
    ''
  );
};

export default UploadDocs;
