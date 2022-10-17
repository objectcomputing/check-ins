import React, {useRef} from "react";
import { Editor } from '@tinymce/tinymce-react';

export default function MarkdownNote(props) {
  const editorRef = useRef(null);
  function saveTextHandler() {
    const text = editorRef.current.getContent();
    console.log(text)
    return text;
  };

  return (
      <>
      <Editor
          apiKey='246ojmsp6c7qtnr9aoivktvi3mi5t7ywuf0vevn6wllfcn9e'
          onInit={(evt, editor) => editorRef.current = editor}
          init={{
            height: 500,
            menubar: false,
            plugins: [
              'advlist', 'autolink', 'lists', 'link', 'image', 'charmap', 'preview',
              'anchor', 'searchreplace', 'visualblocks', 'code', 'fullscreen',
              'insertdatetime', 'media', 'table', 'code', 'help', 'wordcount'
            ],
            toolbar: 'undo redo | blocks | ' +
                'bold italic forecolor | alignleft aligncenter ' +
                'alignright alignjustify | bullist numlist outdent indent | ' +
                'removeformat | help',
            content_style: 'body { font-family:Helvetica,Arial,sans-serif; font-size:14px }'
          }}
      />
        <button onClick={saveTextHandler}>Log content</button>
      </>
  );
}
