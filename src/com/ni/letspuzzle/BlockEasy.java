package com.ni.letspuzzle;

import java.sql.Date;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;



public class BlockEasy extends View {

	private static final int GRID_BG_COLOR = Color.argb(0xff, 0x0, 0x0, 0x0);
	EasyLevel e=new EasyLevel();
	private int delayMillis = 30;
    public int count=0;
    public long endTime1;
	private Context context = null;
	public Date d1;
	private Bitmap backImage;

	private Bitmap screen;

	private int[] blocks;

	private boolean isEvent = false;

	private float _width;

	private float _height;

	private static final int ROWS = 4;

	private static final int COLS = 4;
 
	private int blockWidth;

	private int blockHeight;

	private int imgsCounts;

	private Resources r = null;

	private Paint paint = new Paint();

	private Canvas backCanvas = null;

	private Canvas screenCanvas = null;

	private float rale;

	private boolean running = false;

	private boolean inited = false;

	private int xOffset, yOffset;

	private Bitmap overImage;

	private Rect bufSrcRect = null;

	private RectF bufTarRect = null;

	private int[] gridCols = new int[(COLS + 1) * 4];

	private int[] gridRows = new int[(ROWS + 1) * 4];

	public BlockEasy(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

	}

	public BlockEasy(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (!inited) {
			r = context.getResources();
			init(w, h);
			int i = 0;
			
			for (i = 0; i <= COLS; i++) {
				gridCols[i * COLS + 0] = xOffset + i * blockWidth;
				gridCols[i * COLS + 1] = yOffset;
				gridCols[i * COLS + 2] = i * blockHeight + yOffset;
				gridCols[i * COLS + 3] = ROWS * blockHeight + yOffset;
			}

			for (i = 0; i <= ROWS; i++) {
				gridRows[i * ROWS + 0] = xOffset;
				gridRows[i * ROWS + 1] = i * blockHeight + yOffset;
				gridRows[i * ROWS + 2] = COLS * blockWidth + xOffset;
				gridRows[i * ROWS + 3] = i * blockHeight + yOffset;
			}

			bufSrcRect = new Rect(0, 0, (int) _width, (int) _height);

			bufTarRect = new RectF(0, 0, _width, _height);

			inited = true;
			update();
		}
	}

	
	public void init(int w, int h) {
		setFocusable(true);
		//  Drawable 
		Drawable blockImage = r.getDrawable(R.drawable.troop);
		
		_width = blockImage.getMinimumWidth();
		
		_height = blockImage.getMinimumHeight();
		rale = Math.min(w / _width, h / _height);
		_width = (int) (_width * rale);
		_height = (int) (_height * rale);

		xOffset = (w - (int) _width) >> 1;
		yOffset = (h - (int) _height) >> 1;
		
		blockWidth = (int) (_width / COLS);
		 
		blockHeight = (int) (_height / ROWS);

		// backimage

		getBackImage(r.getDrawable(R.drawable.troop));
		getScreenImage();
		getOverImage(r.getDrawable(R.drawable.angel));

		
		imgsCounts = COLS * ROWS;

		blocks = new int[imgsCounts];
		
		isEvent = false;

		
		for (int i = 0; i < imgsCounts; i++) {
			blocks[i] = i;
		}
		 
		randomPannel();

	}

	private void getBackImage(Drawable src) {
		
		Bitmap bitmap = Bitmap.createBitmap((int) _width, (int) _height
				+ blockHeight, Bitmap.Config.ARGB_8888);
		backCanvas = new Canvas();
		backCanvas.setBitmap(bitmap);
		src.setBounds(0, 0, (int) _width, (int) _height);
		src.draw(backCanvas);
		backImage = bitmap;
	}

	private void getOverImage(Drawable src) {

		Bitmap bitmap = Bitmap.createBitmap((int) _width, (int) _height,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas();
		canvas.setBitmap(bitmap);
		src.setBounds(0, 0, (int) _width, (int) _height);
		src.draw(canvas);
		overImage = bitmap;
		
	}

	private void getScreenImage() {
	
		Bitmap bitmap = Bitmap.createBitmap((int) _width, (int) _height,
				Bitmap.Config.ARGB_8888);
		screenCanvas = new Canvas();
		screenCanvas.setBitmap(bitmap);

		screen = bitmap;
	}

	
	private void randomPannel() {
		 
		for (int i = 0; i < imgsCounts + (double) COLS * Math.random(); i++) {
			 
			int firstPicColNum = (int) ((double) COLS * Math.random());
			int firstPicRowNum = (int) ((double) ROWS * Math.random());
			int anotherPicColNum = (int) ((double) COLS * Math.random());
			int anotherPicRowNum = (int) ((double) ROWS * Math.random());

			
			copyPic(firstPicColNum, firstPicRowNum, 0, ROWS);
			copyPic(anotherPicColNum, anotherPicRowNum, firstPicColNum,
					firstPicRowNum);
			copyPic(0, ROWS, anotherPicColNum, anotherPicRowNum);

			// blocks
			int indexOfFirstPic = blocks[firstPicRowNum * COLS + firstPicColNum];//
			blocks[firstPicRowNum * COLS + firstPicColNum] = blocks[anotherPicRowNum
					* COLS + anotherPicColNum];
			blocks[anotherPicRowNum * COLS + anotherPicColNum] = indexOfFirstPic;
		}

	}

	private void doBufferedImage() {
		
		screenCanvas.drawBitmap(backImage, bufSrcRect, bufTarRect, null);
		if (isEvent) {
			// Screen
			
			screenCanvas.drawBitmap(overImage, null, bufTarRect, null);
			count=1;
			d1 = new Date(0);
			endTime1 =d1.getTime();
			Log.i("The End time is:---------->>>>>>>>", ""+endTime1);
			//int tim=Integer.parseInt(e.tv.getText().toString());		
			//e.tv.setText("win");
			
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int c = paint.getColor();
	 
		canvas.drawBitmap(screen, xOffset, yOffset, null);
		c = paint.getColor();
		if (!isEvent) {
			 
			paint.setColor(GRID_BG_COLOR);
			int i = 0;
			int row_col_count = 0;
			for (i = 0; i <= COLS; i++) {
				row_col_count = i * COLS;
				canvas.drawLine(gridCols[row_col_count],
						gridCols[row_col_count + 0],
						gridCols[row_col_count + 0],
						gridCols[row_col_count + 1], paint);
			}
			row_col_count = 0;
			for (i = 0; i <= ROWS; i++) {
				row_col_count = i * ROWS;
				canvas.drawLine(gridRows[row_col_count],
						gridRows[row_col_count + 0],
						gridRows[row_col_count + 0],
						gridRows[row_col_count + 1], paint);
			}

		}
		paint.setColor(c);
		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN) {
			return true;
		}

		if (isEvent)
			return true;

		int k = (int) ((event.getX() - xOffset) / blockWidth);
		int l = (int) ((event.getY() - yOffset) / blockHeight);

		if (k >= 4)
			k = 3;
		if (l >= 4)
			l = 3;

		// height width
		copyPic(0, 0, 0, ROWS);
		copyPic(k, l, 0, 0);
		copyPic(0, ROWS, k, l);

		int i1 = blocks[0];
		 
		blocks[0] = blocks[l * COLS + k];
		blocks[l * COLS + k] = i1;

		
		for (int j1 = 0; j1 < imgsCounts; j1++) {
			if (blocks[j1] != j1)
				break;
			if (j1 == imgsCounts - 1){
				isEvent = true;
				}

		}

		invalidate();
		return true;
	}

	
	void copyPic(int srcCol, int srcRow, int distCol, int distRow) {
		
		RectF dist = new RectF(srcCol * blockWidth + (distCol - srcCol)
				* blockWidth, srcRow * blockHeight + (distRow - srcRow)
				* blockHeight, srcCol * blockWidth + (distCol - srcCol)
				* blockWidth + blockWidth, srcRow * blockHeight
				+ (distRow - srcRow) * blockHeight + blockHeight);

		try {
			// Bitmap.createBitmap BitmapBitmap
			Bitmap subCopy = Bitmap.createBitmap(backImage,
					srcCol * blockWidth, srcRow * blockHeight, blockWidth,
					blockHeight);
			backCanvas.drawBitmap(subCopy, null, dist, null);

			subCopy.recycle(); // Bitmap

		} catch (OutOfMemoryError ex) {
			// return bitmap.

		}
	}

	
	public boolean isEvent() {
		return isEvent;
	}

	public void setEvent(boolean isEvent) {
		this.isEvent = isEvent;
		int tim=Integer.parseInt(e.tv.getText().toString());		
		e.tv.setText("win");
	}

	private RefreshHandler mRedrawHandler = new RefreshHandler();

	// Handlerֱ
	class RefreshHandler extends Handler {

		public void handleMessage(Message msg) {
			BlockEasy.this.invalidate(); 
			BlockEasy.this.update();
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
		
	}

	public void update() {
		if (running) {
			doBufferedImage();
			mRedrawHandler.sleep(delayMillis);
		}
	}

	public void setUpdateRunningAfterInited(boolean running) {
		this.running = running;
		if (inited) {
			update();

		}
	}

}