#coding=gbk
"""
Assembly:
        ���ͼ��һЩ����ͼ��ļ��ϣ�����ʵ�ֶ���Щͼ���������,�ڲ����û�͸��

Assembly��ʵ�����ݣ�
		ʵ�ֶ������ͼ�εķ�װ�Ͳ���
		ʵ��������߼��ļ��洢
	
Assembly�Ļ����ṹ:
	Assembly:
		name:��ͼ����
		_pos:��ͼ��λ��
		_width:��ͼ��������
		_height:��ͼ������߶�
		_graphList:���л���ͼ��ļ���
		_isDraw:�Ƿ�������ʾ

Assembly���ļ��洢�ṹ:
		����graphics���಻��ֱ�Ӵ洢���ļ��У���ֻ���ֶ�����ת���ɿ��Դ洢�ĸ�ʽ
		�����ֵ����ʽ������ͼ����������ͼ�Ĺؼ���Ϣ�����ֵ�Ĺؼ��б���
		��ʽ��	assDict = { "state":stateDict , "graph":graphInfoList }
					stateDict = {"x" , "y" , "width", "height", "name"}
					graphInfoList = [ graphInfo , ... ]
						graphInfo = {"type" , "content"}
							content = {"x","y","x1","y1","x2","y2","r","outline","fill"}
"""
from time import*
from graphics import*
from cPickle import*

class Assembly:
	def __init__(self,width=0,height=0,pos=Point(0,0)):			
		""" ��ͼ��ʼ�� """
		self._isDraw=False
		self._graphList=[]
		self._pos=pos
		self._width=width
		self._height=height
		self.name="unknown"
	
	def setState(self,pos,width,height):
		"""��ͼλ�á���С����"""
		self.amplifyTo(width,height)
		self.moveTo(pos)
	
	def appendGraphs(self,*graph):
		"""���ͼ����ϳ�һ����ͼ"""
		size=len(graph)
		for i in range(0,size):
			self._graphList.append(graph[i])
	
	def appendAssemblies(self,*assembly):
		"""����ͼ�ϲ��뵱ǰͼ"""
		size=len(assembly)
		for i in range(0,size):
			gsize=len(assembly[i]._graphList)
			for j in range(0,gsize):
				self._graphList.append(assembly[i]._graphList[j])

	def importDict(self,assDict):
		"""���ֵ��е�����ͼ���������С���ֳ�����е���"""
		stateDict=assDict["state"]
		self.name=stateDict["name"]
		p=Point(stateDict["x"],stateDict["y"])
		self._importDict(assDict)
		pos,width,height=self._pos,self._width,self._height
		self._pos,self._width,self._height=p,stateDict["width"],stateDict["height"]
		if width!=0 and height!=0:
			self.amplifyTo(width,height)
			self.moveTo(pos)

	def _importDict(self,assDict):
		"""���ֵ��е�����ͼ���������С���е���"""
		graphInfoList=assDict["graph"]
		self._graphList=[]
		size=len(graphInfoList)					#fileDataList��ʽ��list=[{type,content},...]
		for i in range(0,size):					#���У�content={x0,x1,y0,y1,color0,color1,...}
			type=graphInfoList[i]["type"]							
			content=graphInfoList[i]["content"]
			if type=="Point":
				graph=Point(content["x"],content["y"])
				color=content["outline"]
				graph.setOutline(color)
				self._graphList.append(graph)
			elif type=="Line":
				p0,p1=Point(content["x1"],content["y1"]),Point(content["x2"],content["y2"])
				graph=Line(p0,p1)
				color=content["fill"]
				graph.setFill(color)
				self._graphList.append(graph)
			elif type=="Rectangle":
				p0,p1=Point(content["x1"],content["y1"]),Point(content["x2"],content["y2"])
				graph=Rectangle(p0,p1)
				outline=content["outline"]
				fill=content["fill"]
				graph.setOutline(outline)
				graph.setFill(fill)
				self._graphList.append(graph)
			elif type=="Circle":
				p=Point(content["x"],content["y"])
				graph=Circle(p,content["r"])
				outline=content["outline"]
				fill=content["fill"]
				graph.setOutline(outline)
				graph.setFill(fill)
				self._graphList.append(graph)
			elif type=="Oval":
				p0,p1=Point(content["x1"],content["y1"]),Point(content["x2"],content["y2"])
				graph=Oval(p0,p1)
				outline=content["outline"]
				fill=content["fill"]
				graph.setOutline(outline)
				graph.setFill(fill)
				self._graphList.append(graph)
		
	def exportDict(self):					
		"""������ͼ�е����ֵ�"""
		assDict={"state":{	"x":self._pos.getX(),
							"y":self._pos.getY(),
							"width":self._width,
							"height":self._height,
							"name":self.name}}
		graphInfoList=[]
		list=self._graphList
		size=len(list)
		for i in range(0,size):
			if isinstance(list[i],Point):
				x,y=list[i].getX(),list[i].getY()
				color=list[i].config["outline"]
				graphInfo={"type":"Point", "content":{"x":x,"y":y,"outline":color}}
				graphInfoList.append(graphInfo)
			elif isinstance(list[i],Line):
				x1,y1=list[i].getP1().getX(),list[i].getP1().getY()
				x2,y2=list[i].getP2().getX(),list[i].getP2().getY()			
				color=list[i].config["fill"]
				graphInfo={"type":"Line", "content":{"x1":x1,"y1":y1,"x2":x2,"y2":y2,"fill":color}}
				graphInfoList.append(graphInfo)
			elif isinstance(list[i],Rectangle):
				x1,y1=list[i].getP1().getX(),list[i].getP1().getY()
				x2,y2=list[i].getP2().getX(),list[i].getP2().getY()
				outline=list[i].config["outline"]
				fill=list[i].config["fill"]
				graphInfo={"type":"Rectangle", "content":{"x1":x1,"y1":y1,"x2":x2,"y2":y2,"outline":outline,"fill":fill}}
				graphInfoList.append(graphInfo)
			elif isinstance(list[i],Circle):
				x,y=list[i].getCenter().getX(),list[i].getCenter().getY()
				r=list[i].getRadius()
				outline=list[i].config["outline"]
				fill=list[i].config["fill"]
				graphInfo={"type":"Circle", "content":{"x":x,"y":y,"r":r,"outline":outline,"fill":fill}}
				graphInfoList.append(graphInfo)
			elif isinstance(list[i],Oval):
				x1,y1=list[i].getP1().getX(),list[i].getP1().getY()
				x2,y2=list[i].getP2().getX(),list[i].getP2().getY()
				outline=list[i].config["outline"]
				fill=list[i].config["fill"]
				graphInfo={"type":"Oval", "content":{"x1":x1,"y1":y1,"x2":x2,"y2":y2,"outline":outline,"fill":fill}}
				graphInfoList.append(graphInfo)
		assDict["graph"]=graphInfoList
		return assDict
	
	def save(self,filename):					
		"""�������ļ�"""
		f=file(filename, 'w')
		dump(self.exportDict(),f)
		f.close()
	
	def load(self,filename):					
		"""���ļ��ж�ȡ�����سɹ����"""
		try:
			f=file(filename)
			list=load(f)
			self.importDict(list)
			f.close()
			return True
		except:
			return False
	
	def move(self,dx,dy):						
		"""�����ƶ���ͼ"""
		self._pos.move(dx,dy)
		size=len(self._graphList)
		for i in range(0,size):
			self._graphList[i].move(dx,dy)
	
	def amplify(self,xscale,yscale,center=None):					
		"""��scale�����Ŵ�ͼ"""
		if center==None:
			center=self._pos
		self._width*=xscale
		self._height*=yscale
		dx=(xscale-1)*(self._pos.getX()-center.getX())
		dy=(yscale-1)*(self._pos.getY()-center.getY())
		self._pos.move(dx,dy)
		if len(self._graphList)!=0:
			canvas=self._graphList[0].canvas	#���浱ǰ����
			willDraw=self._isDraw
			self.undraw()
			assDict=self.exportDict()
			list=assDict["graph"]
			size=len(list)
			for i in range(0,size):
				type=list[i]["type"]						
				content=list[i]["content"]
				if type=="Point":
					content["x"]=(content["x"]-center.getX())*xscale+center.getX()
					content["y"]=(content["y"]-center.getY())*yscale+center.getY()
				elif type=="Circle":
					list[i]["type"]="Oval"
					x1,y1=content["x"]-content["r"],content["y"]-content["r"]
					x2,y2=content["x"]+content["r"],content["y"]+content["r"]
					content["x1"]=(x1-center.getX())*xscale+center.getX()
					content["y1"]=(y1-center.getY())*yscale+center.getY()
					content["x2"]=(x2-center.getX())*xscale+center.getX()
					content["y2"]=(y2-center.getY())*yscale+center.getY()
					del content["x"],content["y"]
				elif type=="Line" or type=="Rectangle" or type=="Oval":
					content["x1"]=(content["x1"]-center.getX())*xscale+center.getX()
					content["y1"]=(content["y1"]-center.getY())*yscale+center.getY()
					content["x2"]=(content["x2"]-center.getX())*xscale+center.getX()
					content["y2"]=(content["y2"]-center.getY())*yscale+center.getY()
							
			self._importDict(assDict)
			if willDraw:
				self.draw(canvas)
	
	def moveTo(self,p):
		"""�ƶ���ͼ��ָ��λ��"""
		dx=p.getX()-self._pos.getX()
		dy=p.getY()-self._pos.getY()
		self.move(dx,dy)
	
	def amplifyTo(self,width,height,center=None):
		"""�Ŵ���ͼ��ָ����С"""
		if self._width==0 or self._height==0:	#Ĭ��Ϊԭʼͼ��
			self._width=width
			self._height=height
		else:
			xscale=width*1.0/self._width
			yscale=height*1.0/self._height
			self.amplify(xscale,yscale,center)
		
	def draw(self,window):						
		"""����ǰͼ��"""
		self._isDraw=True
		size=len(self._graphList)
		for i in range(0,size):
			self._graphList[i].draw(window)
	
	def undraw(self):							
		"""������ǰͼ��"""
		if self._isDraw:
			size=len(self._graphList)
			for i in range(0,size):
				self._graphList[i].undraw()
			self._isDraw=False
	
	def inside(self,p):
		"""��p�Ƿ���ͼ����"""
		xjudge=(self._pos.getX()-p.getX())*(self._pos.getX()+self._width-p.getX())
		yjudge=(self._pos.getY()-p.getY())*(self._pos.getY()+self._height-p.getY())
		return xjudge<=0 and yjudge<=0
		
	def isDraw(self):
		return self._isDraw
	
	def pos(self):
		return self._pos
	
	def width(self):
		return self._width
	
	def height(self):
		return self._height


def test():
	w=55
	win=GraphWin('Shapes',16*w,9*w)
	win.setBackground('white')
	gp=Assembly()
	gp2=Assembly()
	#gp2.load("bin/remove.dat")
	#gp2.save("bin/restart.dat")
	gp2.draw(win)
	#gp2.amplifyTo(w,w,Point(4*w,3*w))
	gp.load("bin/box2.dat")
	gp.amplifyTo(w,w)
	gp.draw(win)
	#f=file('data.txt','w')
	#f.write(repr(gp2.exportDict()))
	#f.close()
	#gp.save("bin/man.dat")
	#print gp.exportDict()
	pause=raw_input()
	
if __name__ == "__main__":
    test()
