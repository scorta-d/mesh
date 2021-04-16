package mesh;

import static javax.swing.UIManager.setLookAndFeel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

public class MainForm extends JFrame {
	private static final long serialVersionUID = 1L;

	JTable mGrid;
	PlayGroundModel mDataModel;

	private JScrollPane mGridView;
	private JButton mRandom;
	private JButton mClear;
	private JButton mProbCalc;
	public List<Cell> mLessLikelyCells = new ArrayList<Cell>();

	private JButton getClear() {
		if (mClear == null) {
			mClear = new JButton();
			mClear.setText("Clear");
			mClear.addActionListener(new ClearAction(this));
		}
		return mClear;
	}

	private JButton getRandom() {
		if (mRandom == null) {
			mRandom = new JButton();
			mRandom.setText("Random");
			mRandom.addActionListener(new RandomAction(this));

		}
		return mRandom;

	}

	public Component getGridView() {
		if (mGridView == null) {
			mGridView = new JScrollPane(getGrid());
			mGridView.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

		}
		return mGridView;
	}

	public JTable getGrid() {
		if (mGrid == null) {
			mGrid = new JTable();

			setDataModel(new PlayGroundModel(this));
			mGrid.setModel(getDataModel());
			mGrid.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			mGrid.addKeyListener(new CellKeyListener(this));
			mGrid.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			mGrid.setColumnSelectionAllowed(true);
			mGrid.setRowSelectionAllowed(true);
			mGrid.setTableHeader(null);
			mGrid.addMouseWheelListener(new MouseWheelListener() {
				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					String[] variants = { "", ".", "1", "2", "3", "4", "5", "6", "7", "8", "Flag" };
					int row = mGrid.rowAtPoint(e.getPoint());
					int col = mGrid.columnAtPoint(e.getPoint());

					if (col < 0 || row < 0) {
						return;
					}
					String cur = getDataModel().matrix[row][col];
					boolean found = false;
					int iVar = 0;
					for (; iVar < variants.length; iVar++) {
						if (variants[iVar].equals(cur)) {
							found = true;
							break;
						}
						iVar++;
					}
					if (!found) {
						iVar = 1;
					}
					int rotation = e.getWheelRotation();
					System.out.println(rotation);
					if (rotation < 0) {
						iVar++;

					} else {
						iVar--;

					}
					if (iVar < 1) {
						iVar = 1;
					} else if (iVar > variants.length - 1) {
						iVar = variants.length - 1;
					}

					getDataModel().matrix[row][col] = variants[iVar];
					getDataModel().fireTableCellUpdated(row, col);
				}

			});

			final TableCellRenderer rndr = mGrid.getDefaultRenderer(Object.class);
			mGrid.setDefaultRenderer(Object.class, new OurCellRenderer(this, rndr));

		}
		return mGrid;

	}

	public MainForm() {
		try {
			UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
			for (UIManager.LookAndFeelInfo look : looks) {
				System.out.println(look.getClassName());
			}

			setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
//		Thread db = new Thread(new Runnable() {
//			@Override
//			public void run() {
//			}
//		});
//		db.start();

		setSize(470, 520);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(getGridView(), GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(

						layout.createParallelGroup() //
								.addComponent(getRandom()) //
								.addComponent(getClear()) //
								.addComponent(getProbCalc()))//
//				.addGroup(
//
//						layout.createParallelGroup() //
//								//
//				)

		);

		layout.setHorizontalGroup( //
				layout.createParallelGroup() //
						.addComponent(getGridView(), 100, GroupLayout.DEFAULT_SIZE, Integer.MAX_VALUE) //
						.addGroup(

								layout.createSequentialGroup() //
										.addComponent(getProbCalc())//
										.addComponent(getRandom()) //
										.addComponent(getClear()) //

						//
						));

	}

	public static void main(String[] argv) {
		MainForm form = new MainForm();
		form.setVisible(true);

	}

	public void setDataModel(PlayGroundModel dataModel) {
		mDataModel = dataModel;
	}

	public PlayGroundModel getDataModel() {
		return mDataModel;
	}

	public void setGridView(JScrollPane gridView) {
		mGridView = gridView;
	}

	private JButton getProbCalc() {
		if (mProbCalc == null) {
			mProbCalc = new JButton("Calc");
			mProbCalc.addActionListener(new ProbabilityAction(this));
		}
		return mProbCalc;
	}

	void clear(int mod) {
		int size = getDataModel().getSize();

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (mod > 0) {
					String str = getDataModel().matrix[i][j];
					if ((str.indexOf("%") >= 0) || ("Flag".equals(str))) {
						getDataModel().matrix[i][j] = "";
					}

				} else {
					getDataModel().matrix[i][j] = "";
				}

			}
		}
		getDataModel().fireTableDataChanged();
	}

}
