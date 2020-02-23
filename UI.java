    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == close || e.getSource() == closeButton) {
            if (edit) {
                Object[] options = {"Save and exit", "No Save and exit", "Return"};
                int n = JOptionPane.showOptionDialog(this, "Do you want to save the file ?", "Question",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
                if (n == 0) {
                    saveFile();
                    this.dispose();
                } else if (n == 1) {
                    this.dispose();
                }
            } else {
                this.dispose();
            }
        }
        else if (e.getSource() == newFile || e.getSource() == newButton) {
            if (edit) {
                Object[] options = {"Save", "No Save", "Return"};
                int n = JOptionPane.showOptionDialog(this, "Do you want to save the file at first ?", "Question",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
                if (n == 0) {// save
                    saveFile();
                    edit = false;
                } else if (n == 1) {
                    edit = false;
                    FEdit.clear(textArea);
                }
            } else {
                FEdit.clear(textArea);
            }

        } // If the source was the "open" option
        else if (e.getSource() == openFile || e.getSource() == openButton) {
            JFileChooser open = new JFileChooser(); // open up a file chooser (a dialog for the user to  browse files to open)
            int option = open.showOpenDialog(this); // get the option that the user selected (approve or cancel)

            /*
             * NOTE: because we are OPENing a file, we call showOpenDialog~ if
             * the user clicked OK, we have "APPROVE_OPTION" so we want to open
             * the file
             */
            if (option == JFileChooser.APPROVE_OPTION) {
                FEdit.clear(textArea); // clear the TextArea before applying the file contents
                try {
                    File openFile = open.getSelectedFile();
                    setTitle(openFile.getName() + " | " + SimpleJavaTextEditor.NAME);
                    Scanner scan = new Scanner(new FileReader(openFile.getPath()));
                    while (scan.hasNext()) {
                        textArea.append(scan.nextLine() + "\n");
                    }

                    enableAutoComplete(openFile);
                } catch (Exception ex) { // catch any exceptions, and...
                    // ...write to the debug console
                    System.err.println(ex.getMessage());
                }
            }
        } // If the source of the event was the "save" option
        else if (e.getSource() == saveFile || e.getSource() == saveButton) {
            saveFile();
        }// If the source of the event was the "Bold" button
        else if (e.getSource() == boldButton) {
            if (textArea.getFont().getStyle() == Font.BOLD) {
                textArea.setFont(textArea.getFont().deriveFont(Font.PLAIN));
            } else {
                textArea.setFont(textArea.getFont().deriveFont(Font.BOLD));
            }
        }// If the source of the event was the "Italic" button
        else if (e.getSource() == italicButton) {
            if (textArea.getFont().getStyle() == Font.ITALIC) {
                textArea.setFont(textArea.getFont().deriveFont(Font.PLAIN));
            } else {
                textArea.setFont(textArea.getFont().deriveFont(Font.ITALIC));
            }
        }
        // Clear File (Code)
        if (e.getSource() == clearFile || e.getSource() == clearButton) {

            Object[] options = {"Yes", "No"};
            int n = JOptionPane.showOptionDialog(this, "Are you sure to clear the text Area ?", "Question",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
            if (n == 0) {// clear
                FEdit.clear(textArea);
            }
        }
        // Find
        if (e.getSource() == quickFind || e.getSource() == quickButton) {
            new Find(textArea);
        } // About Me
        else if (e.getSource() == aboutMe || e.getSource() == aboutMeButton) {
            new About(this).me();
        } // About Software
        else if (e.getSource() == aboutSoftware || e.getSource() == aboutButton) {
            new About(this).software();
        }
    }

    class SelectAllAction extends AbstractAction {

        /**
         * Used for Select All function
         */
        private static final long serialVersionUID = 1L;

        public SelectAllAction(String text, ImageIcon icon, String desc, Integer mnemonic, final JTextArea textArea) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        public void actionPerformed(ActionEvent e) {
            textArea.selectAll();
        }
    }

    private void saveFile() {
        // Open a file chooser
        JFileChooser fileChoose = new JFileChooser();
        // Open the file, only this time we call
        int option = fileChoose.showSaveDialog(this);

        /*
             * ShowSaveDialog instead of showOpenDialog if the user clicked OK
             * (and not cancel)
         */
        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                File openFile = fileChoose.getSelectedFile();
                setTitle(openFile.getName() + " | " + SimpleJavaTextEditor.NAME);

                BufferedWriter out = new BufferedWriter(new FileWriter(openFile.getPath()));
                out.write(textArea.getText());
                out.close();

                enableAutoComplete(openFile);
                edit = false;
            } catch (Exception ex) { // again, catch any exceptions and...
                // ...write to the debug console
                System.err.println(ex.getMessage());
            }
        }
    }
    DropTargetListener dropTargetListener = new DropTargetListener() {

        @Override
        public void dragEnter(DropTargetDragEvent e) {
        }

        @Override
        public void dragExit(DropTargetEvent e) {
        }

        @Override
        public void dragOver(DropTargetDragEvent e) {
        }

        @Override
        public void drop(DropTargetDropEvent e) {
            if (edit) {
                Object[] options = {"Save", "No Save", "Return"};
                int n = JOptionPane.showOptionDialog(UI.this, "Do you want to save the file at first ?", "Question",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
                if (n == 0) {// save
                    UI.this.saveFile();
                    edit = false;
                } else if (n == 1) {
                    edit = false;
                    FEdit.clear(textArea);
                } else if (n == 2) {
                    e.rejectDrop();
                    return;
                }
            }
            try {
                Transferable tr = e.getTransferable();
                DataFlavor[] flavors = tr.getTransferDataFlavors();
                for (int i = 0; i < flavors.length; i++) {
                    if (flavors[i].isFlavorJavaFileListType()) {
                        e.acceptDrop(e.getDropAction());

                        try {
                            String fileName = tr.getTransferData(flavors[i]).toString().replace("[", "").replace("]", "");

                            // Allowed file filter extentions for drag and drop
                            boolean extensionAllowed = false;
                            for (int j = 0; j < dragDropExtensionFilter.length; j++) {
                                if (fileName.endsWith(dragDropExtensionFilter[j])) {
                                    extensionAllowed = true;
                                    break;
                                }
                            }
                            if (!extensionAllowed) {
                                JOptionPane.showMessageDialog(UI.this, "This file is not allowed for drag & drop", "Error", JOptionPane.ERROR_MESSAGE);

                            } else {
                                FileInputStream fis = new FileInputStream(new File(fileName));
                                byte[] ba = new byte[fis.available()];
                                fis.read(ba);
                                textArea.setText(new String(ba));
                                fis.close();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        e.dropComplete(true);
                        return;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            e.rejectDrop();
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent e) {
        }
    };

}
