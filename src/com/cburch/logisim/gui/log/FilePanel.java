/*******************************************************************************
 * This file is part of logisim-evolution.
 *
 *   logisim-evolution is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   logisim-evolution is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with logisim-evolution.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Original code by Carl Burch (http://www.cburch.com), 2011.
 *   Subsequent modifications by :
 *     + Haute École Spécialisée Bernoise
 *       http://www.bfh.ch
 *     + Haute École du paysage, d'ingénierie et d'architecture de Genève
 *       http://hepia.hesge.ch/
 *     + Haute École d'Ingénierie et de Gestion du Canton de Vaud
 *       http://www.heig-vd.ch/
 *   The project is currently maintained by :
 *     + REDS Institute - HEIG-VD
 *       Yverdon-les-Bains, Switzerland
 *       http://reds.heig-vd.ch
 *******************************************************************************/

package com.cburch.logisim.gui.log;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.cburch.logisim.data.Value;
import com.cburch.logisim.util.JFileChoosers;
import com.cburch.logisim.util.StringUtil;

class FilePanel extends LogPanel {
	private class Listener implements ActionListener, ModelListener {
		public void actionPerformed(ActionEvent event) {
			Object src = event.getSource();
			if (src == enableButton) {
				getModel().setFileEnabled(!getModel().isFileEnabled());
			} else if (src == selectButton) {
				int result = chooser.showSaveDialog(getLogFrame());
				if (result != JFileChooser.APPROVE_OPTION)
					return;
				File file = chooser.getSelectedFile();
				if (file.exists() && (!file.canWrite() || file.isDirectory())) {
					JOptionPane.showMessageDialog(
							getLogFrame(),
							StringUtil.format(
									Strings.get("fileCannotWriteMessage"),
									file.getName()),
							Strings.get("fileCannotWriteTitle"),
							JOptionPane.OK_OPTION);
					return;
				}
				if (file.exists() && file.length() > 0) {
					String[] options = { Strings.get("fileOverwriteOption"),
							Strings.get("fileAppendOption"),
							Strings.get("fileCancelOption"), };
					int option = JOptionPane.showOptionDialog(getLogFrame(),
							StringUtil.format(Strings.get("fileExistsMessage"),
									file.getName()), Strings
									.get("fileExistsTitle"), 0,
							JOptionPane.QUESTION_MESSAGE, null, options,
							options[0]);
					if (option == 0) {
						try {
							FileWriter delete = new FileWriter(file);
							delete.close();
						} catch (IOException e) {
						}
					} else if (option == 1) {
						// do nothing
					} else {
						return;
					}
				}
				getModel().setFile(file);
			} else if (src == headerCheckBox) {
				getModel().setFileHeader(headerCheckBox.isSelected());
			}
		}

		private void computeEnableItems(Model model) {
			if (model.isFileEnabled()) {
				enableLabel.setText(Strings.get("fileEnabled"));
				enableButton.setText(Strings.get("fileDisableButton"));
			} else {
				enableLabel.setText(Strings.get("fileDisabled"));
				enableButton.setText(Strings.get("fileEnableButton"));
			}
		}

		public void entryAdded(ModelEvent event, Value[] values) {
		}

		public void filePropertyChanged(ModelEvent event) {
			Model model = getModel();
			computeEnableItems(model);

			File file = model.getFile();
			fileField.setText(file == null ? "" : file.getPath());
			enableButton.setEnabled(file != null);

			headerCheckBox.setSelected(model.getFileHeader());
		}

		public void selectionChanged(ModelEvent event) {
		}
	}

	private static final long serialVersionUID = 1L;

	private Listener listener = new Listener();
	private JLabel enableLabel = new JLabel();
	private JButton enableButton = new JButton();
	private JLabel fileLabel = new JLabel();
	private JTextField fileField = new JTextField();
	private JButton selectButton = new JButton();
	private JCheckBox headerCheckBox = new JCheckBox();
	private JFileChooser chooser = JFileChoosers.create();

	public FilePanel(LogFrame frame) {
		super(frame);

		JPanel filePanel = new JPanel(new GridBagLayout());
		GridBagLayout gb = (GridBagLayout) filePanel.getLayout();
		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(fileLabel, gc);
		filePanel.add(fileLabel);
		gc.weightx = 1.0;
		gb.setConstraints(fileField, gc);
		filePanel.add(fileField);
		gc.weightx = 0.0;
		gb.setConstraints(selectButton, gc);
		filePanel.add(selectButton);
		fileField.setEditable(false);
		fileField.setEnabled(false);

		setLayout(new GridBagLayout());
		gb = (GridBagLayout) getLayout();
		gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.weightx = 1.0;
		gc.gridy = GridBagConstraints.RELATIVE;
		JComponent glue;
		glue = new JPanel();
		gc.weighty = 1.0;
		gb.setConstraints(glue, gc);
		add(glue);
		gc.weighty = 0.0;
		gb.setConstraints(enableLabel, gc);
		add(enableLabel);
		gb.setConstraints(enableButton, gc);
		add(enableButton);
		glue = new JPanel();
		gc.weighty = 1.0;
		gb.setConstraints(glue, gc);
		add(glue);
		gc.weighty = 0.0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(filePanel, gc);
		add(filePanel);
		gc.fill = GridBagConstraints.NONE;
		glue = new JPanel();
		gc.weighty = 1.0;
		gb.setConstraints(glue, gc);
		add(glue);
		gc.weighty = 0.0;
		gb.setConstraints(headerCheckBox, gc);
		add(headerCheckBox);
		glue = new JPanel();
		gc.weighty = 1.0;
		gb.setConstraints(glue, gc);
		add(glue);
		gc.weighty = 0.0;

		enableButton.addActionListener(listener);
		selectButton.addActionListener(listener);
		headerCheckBox.addActionListener(listener);
		modelChanged(null, getModel());
		localeChanged();
	}

	@Override
	public String getHelpText() {
		return Strings.get("fileHelp");
	}

	@Override
	public String getTitle() {
		return Strings.get("fileTab");
	}

	@Override
	public void localeChanged() {
		listener.computeEnableItems(getModel());
		fileLabel.setText(Strings.get("fileLabel") + " ");
		selectButton.setText(Strings.get("fileSelectButton"));
		headerCheckBox.setText(Strings.get("fileHeaderCheck"));
	}

	@Override
	public void modelChanged(Model oldModel, Model newModel) {
		if (oldModel != null)
			oldModel.removeModelListener(listener);
		if (newModel != null) {
			newModel.addModelListener(listener);
			listener.filePropertyChanged(null);
		}
	}

}
