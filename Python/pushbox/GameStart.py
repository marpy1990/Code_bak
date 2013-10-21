#coding=gbk
"""
Game:
	��������Ϸ�ı���
	��ʵ��������һ����Ϸ��ͼ�༭���ģ���ѩ��world editor���ҵ����룩������ʱ�����ޣ����ԾͿ���

Game��ʵ�����ݣ�
	��ͼ����
	ִ����Ϸ
		
Game����������:
	1.	��ʼ
	2.	�����ͼ����
	3.	���Ƶ�ͼ
	4.	while ��Ӧ�������
			������:
				->�����ƶ�:
					�����Է���
					if ���� then �ƶ�����
				->�����ƶ�:
					�����Է���
					if ���� then �ƶ�����
				->������ť
					��ť��Ӧ
			ʤ����������:
				if ʤ��:
					׼����һ�ŵ�ͼ
					��ת��2

Map�Ľṹ:
	map={"size":net, "state":a[i][j], "flag":[net,...]}
		net={"i":i , "j":j}
		a[i][j] = 0:blank
				= 1:man
				= 2:block
				= 3:box
""" 
from functools import*
from Assembly import*
from graphics import*

def tryload(func):
	"""try...except...����ģ��"""
	@wraps(func)
	def _tryload(self,*args,**kwargs):
		try:
			func(self,*args,**kwargs)
		except:
			try:
				self.map="map/level_1.dat"
				func(self,*args,**kwargs)
			except:
				print "�ӵ��أ�һ�ŵ�ͼ��û�У���ë��������"
	return _tryload

"""��λ����ʵ�������֮���ת��"""		
class Locator(Assembly):
	def __init__(self,m=1,n=1,width=0,height=0,pos=Point(0,0)):
		Assembly.__init__(self,width,height,pos)
		self._m=m
		self._n=n
	
	def pointToNet(self,p):
		"""��õ�p���ڵ���������{i,j}"""
		netwidth=self._width*1.0/self._n
		j=int((p.getX()-self._pos.getX())/netwidth)
		netheight=self._height*1.0/self._m
		i=int((p.getY()-self._pos.getY())/netheight)
		return {"i":i,"j":j}
		
	def nearNetPoint(self,p):
		"""ȡ�þ����p����ĸ������"""
		netwidth=self._width*1.0/self._n
		m=int((p.getX()-self._pos.getX())/netwidth+0.5)
		x=m*netwidth+self._pos.getX()
		
		netheight=self._height*1.0/self._m
		n=int((p.getY()-self._pos.getY())/netheight+0.5)
		y=n*netheight+self._pos.getY()
		return Point(x,y)
	
	def netToPoint(self,i,j):
		"""ȡ�ø��[i,j]����������"""
		netwidth=self._width*1.0/self._n
		x=self._pos.getX()+j*netwidth
		netheight=self._height*1.0/self._m
		y=self._pos.getY()+i*netheight
		return Point(x,y)

class Game:
	def __init__(self):
		w=50
		self.win=GraphWin('Game',14*w,12*w)
		self.win.setBackground('white')
		self.interval=w
		self.map="map/level_1.dat"
		self.locator=None		#����λ��
		self.state=[]
		self.flag=[]
		self.actor=[]			#����map�ϵĶ���
		#��ͼ��m*n��
		self.m=0
		self.n=0
		
	def start(self):
		w=self.interval
		self.restart=Assembly(w,w,Point(13*w,11*w))
		self.restart.load("bin/restart.dat")
		self.restart.draw(self.win)
		while True:
			self._start()		#һ������
			for list in self.actor:
				for g in list:
					g.undraw()
			for g in self.flaglist:
				g.undraw()
			self.frame.undraw()
	
	def _start(self):
		self.load()
		self.drawMap()
		loc=self.locator
		while True:
			p=self.win.getMouse()
			if loc.inside(p):		#������
				self.dealMove(p)
			elif self.restart.inside(p):
				break
			
			if self.victory():		#ʤ����������
				tail=self.map.split('_')[1]
				digit=int(tail.split('.')[0])+1
				self.map="map/level_"+str(digit)+".dat"
				break
	
	def victory(self):
		v=True
		for f in self.flag:
			if self.state[f["i"]][f["j"]]!=3:
				v=False
				break
		return v
		
	def _manPos(self):			#man��λ��
		for i in range(0,self.m):
			for j in range(0,self.n):
				if self.state[i][j]==1:
					return {"i":i, "j":j}
		print "�ӵ���������ô������ͼ����"
		
	def dealMove(self,p):
		loc=self.locator
		state=self.state
		flag=self.flag
		actor=self.actor
		man=self._manPos()		#man������λ��
		net=loc.pointToNet(p)	#p������λ��
		if state[net["i"]][net["j"]]==0:	#�ƶ�man
			if self._canMoveMan(man,net):
				#�ƶ�man
				g=actor[man["i"]][man["j"]]
				actor[man["i"]][man["j"]]=actor[net["i"]][net["j"]]
				actor[net["i"]][net["j"]]=g
				g.moveTo(loc.netToPoint(net["i"],net["j"]))
				#�޸�״̬
				state[man["i"]][man["j"]]=0
				state[net["i"]][net["j"]]=1
		elif state[net["i"]][net["j"]]==3:	#�ƶ�box
			if self._canMoveBox(man,net):
				dir=self._boxDir(man,net)
				#�ƶ�box
				g1=actor[net["i"]][net["j"]]
				actor[net["i"]][net["j"]]=actor[dir["i"]][dir["j"]]
				actor[dir["i"]][dir["j"]]=g1
				g1.moveTo(loc.netToPoint(dir["i"],dir["j"]))
				#�ƶ�man
				g2=actor[man["i"]][man["j"]]
				actor[man["i"]][man["j"]]=actor[net["i"]][net["j"]]
				actor[net["i"]][net["j"]]=g2
				g2.moveTo(loc.netToPoint(net["i"],net["j"]))
				#�޸�״̬
				state[dir["i"]][dir["j"]]=3
				state[man["i"]][man["j"]]=0
				state[net["i"]][net["j"]]=1
		
	def _boxDir(self,man,net):
		di=net["i"]-man["i"]
		dj=net["j"]-man["j"]
		if di==0:
			if dj<0:
				return {"i":net["i"], "j":net["j"]-1}
			elif dj>0:
				return {"i":net["i"], "j":net["j"]+1}
		if dj==0:
			if di>0:
				return {"i":net["i"]+1, "j":net["j"]}
			elif di<0:
				return {"i":net["i"]-1, "j":net["j"]}
		return None
		
	def _canMoveBox(self,man,net):
		dir=self._boxDir(man,net)
		if not dir:
			return False
		if not dir["i"] in range(0,self.m):
			return False
		if not dir["j"] in range(0,self.n):
			return False
		if self.state[dir["i"]][dir["j"]]!=0:
			return False
		return True
	
	def matrix(self,m,n,val):	#����m*n�Ķ�ά����
		a=[]
		for i in range(0,m):
			tmp=[]
			for j in range(0,n):
				tmp.append(val)
			a.append(tmp)
		return a
	
	def _flood(self,judge,i,j):
		judge[i][j]=True
		for di in [-1,1]:
			if i+di>=0 and i+di<self.m:
				if (not judge[i+di][j]) and self.state[i+di][j]==0:
					self._flood(judge,i+di,j)
		for dj in [-1,1]:
			if j+dj>=0 and j+dj<self.n:
				if (not judge[i][j+dj]) and self.state[i][j+dj]==0:
					self._flood(judge,i,j+dj)

				
	def _canMoveMan(self,man,net):
		judge=self.matrix(self.m,self.n,False)	#m*n��judge map
		self._flood(judge,man["i"],man["j"])
		return judge[net["i"]][net["j"]]
	
	
	@tryload					#��û�е�ͼ���õ�һ�ŵ�ͼ
	def load(self):
		f=file(self.map)
		str_dct=f.read()
		dct=eval(str_dct,{"__builtins__":None},{})	#��Ҫ�ǲ�������ͼ�༭���ˣ������Ǳ����ֶ�д���ͼ
		f.close()

		self.m=dct["size"]["i"]
		self.n=dct["size"]["j"]
		self.state=dct["state"]
		self.flag=dct["flag"]
		#ȷ����������
		w=self.interval
		m,n=self.m,self.n
		if n>=m:
			width=10*w*1.0
			height=width*m/n
			pos=Point(w,w+(10*w-height)/2.0)
		else:
			height=10*w*1.0
			width=height*n/m
			pos=Point(w+(10*w-width)/2.0,w)
		self.locator=Locator(m,n,width,height,pos)	#װ�ض�λ��
		
	def drawMap(self):	
		loc=self.locator
		self.frame=Rectangle(loc.netToPoint(0,0),loc.netToPoint(loc._m,loc._n))
		self.frame.setOutline("black")
		#self.frame.setFill(color_rgb(0,255,255))
		self.frame.draw(self.win)
		w=self.locator._width*1.0/self.locator._n
		#��flag
		self.flaglist=[]
		for net in self.flag:
			pos=loc.netToPoint(net["i"],net["j"])
			g=Assembly(w,w,pos)
			g.load("bin/flag.dat")
			g.draw(self.win)
			self.flaglist.append(g)
		#��ͼ��
		self.actor=self.matrix(self.m,self.n,None)
		for i in range(0,self.m):
			for j in range(0,self.n):
				pos=loc.netToPoint(i,j)
				g=Assembly(w,w,pos)
				if self.state[i][j]==1:				#����man
					g.load("bin/man.dat")
				elif self.state[i][j]==2:			#����block
					g.load("bin/block.dat")
				elif self.state[i][j]==3:			#����box
					g.load("bin/box2.dat")
				g.draw(self.win)
				self.actor[i][j]=g					#װ��ؼ�

def game():
	exe=Game()
	exe.start()

if __name__ == "__main__":
    game()
