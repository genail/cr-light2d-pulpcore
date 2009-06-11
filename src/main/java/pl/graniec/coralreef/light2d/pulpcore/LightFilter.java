/**
 * Copyright (c) 2009, Coral Reef Project
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of the Coral Reef Project nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package pl.graniec.coralreef.light2d.pulpcore;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import pl.graniec.coralreef.geometry.Geometry;
import pl.graniec.coralreef.geometry.Point2;
import pulpcore.image.CoreImage;
import pulpcore.image.filter.Filter;

/**
 * @author Piotr Korzuszek <piotr.korzuszek@gmail.com>
 *
 */
public class LightFilter extends Filter {

	/** Geometry of the light */
	private Geometry lightGeometry;
	/** java.awt.BufferedImage to help making the mask */
	private BufferedImage mask;
	
	/**
	 * 
	 */
	public LightFilter() {
//		image = new BufferedImage(Stage.getWidth(), Stage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		mask = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
	}
	
	/* (non-Javadoc)
	 * @see pulpcore.image.filter.Filter#copy()
	 */
	@Override
    public Filter copy() {
        // TODO Auto-generated method stub
        return null;
    }
	
	private void createMask(CoreImage input) {
		
		if (input != null && (mask == null || input.getWidth() != mask.getWidth() || input.getHeight() != mask.getHeight())) {
			mask = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
		}
		
		final Graphics2D g = mask.createGraphics();
		
		// clear image
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, mask.getWidth(), mask.getHeight());
		
		// make the polygon
		final Polygon poly = new Polygon();
		
		for (Point2 p : lightGeometry.getVerticles()) {
			poly.addPoint((int) p.x, (int) p.y);
		}
		
		g.setColor(Color.WHITE);
		g.fillPolygon(poly);
		
		g.dispose();
	}
	
	public void testMask() {
		createMask(null);
		try {
			ImageIO.write(mask, "PNG", new File("/tmp/mask.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(mask.getRaster().getDataBuffer().getClass());
	}
	
	public static void main(String[] args) {
		LightFilter filter = new LightFilter();
		
		Geometry geom = new Geometry();
		geom.addVerticle(new Point2(0, 0));
		geom.addVerticle(new Point2(100, 50));
		geom.addVerticle(new Point2(100, 100));
		
		filter.setLightGeometry(geom);
		filter.testMask();
	}
    
    /* (non-Javadoc)
     * @see pulpcore.image.filter.Filter#filter(pulpcore.image.CoreImage, pulpcore.image.CoreImage)
     */
    @Override
    protected void filter(CoreImage input, CoreImage output) {
        
        if (lightGeometry == null) {
            return;
        }
        
        final int[] srcPixels = input.getData();
        final int[] dstPixels = output.getData();
        
        // create the mask
        createMask(input);
        
        final int[] maskPixels = ((DataBufferInt) mask.getData().getDataBuffer()).getData();
        
        // copy pixels
        for (int i = 0; i < srcPixels.length; ++i) {
        	
        	if (maskPixels[i] == 0xFF000000) {
        		dstPixels[i] = 0x0;
        	} else {
        		dstPixels[i] = srcPixels[i];
        	}
        }
        
    }

    public void setLightGeometry(Geometry lightGeometry) {
        this.lightGeometry = lightGeometry;
        
        setDirty();
    }

}
