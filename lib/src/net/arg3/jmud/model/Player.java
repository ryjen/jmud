/**
 * Project: jMUD
 * Date: 2009-09-12
 * Package: net.arg3.jmud
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.model;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.arg3.jmud.Argument;
import net.arg3.jmud.Attack;
import net.arg3.jmud.Flag;
import net.arg3.jmud.Persistance;
import net.arg3.jmud.annotations.FlagValue;
import net.arg3.jmud.enums.DamType;
import net.arg3.jmud.interfaces.IExecutable;
import net.arg3.jmud.interfaces.ITerminal;
import net.arg3.jmud.interfaces.ITickable;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.event.PostLoadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
@Entity
@Table(name = "player")
@Inheritance(strategy = InheritanceType.JOINED)
@OnDelete(action = OnDeleteAction.CASCADE)
public class Player extends net.arg3.jmud.model.Character implements ITickable {

	private static final long serialVersionUID = 1L;
	@FlagValue
	public static final long DELETING = (1L << 0);
	@FlagValue
	public static final long TITLEBAR = (1L << 1);
	@FlagValue
	public static final long HINTS = (1L << 2);
	private static final Logger log = LoggerFactory.getLogger(Player.class);
	private static Set<Player> list = null;

	public static Player getByName(String name) {

		Player p;
		try {
			Session sess = Persistance.getSession();
			Transaction tx = sess.beginTransaction();

			Query q = sess.createQuery("from Player p where p.name = '" + name
					+ "'");

			p = (Player) q.uniqueResult();

			tx.commit();
			return p;

		} catch (HibernateException ex) {
			log.error(ex.getMessage());
			return null;
		}
	}

	public static synchronized Set<Player> getPlaying() {
		if (list == null) {
			// list = Collections.synchronizedSet(new HashSet<Player>());
			list = new HashSet<Player>();
		}
		return list;
	}

	private final Queue<java.lang.Object[]> commandQueue = new ConcurrentLinkedQueue<java.lang.Object[]>();

	private Account account;
	private Profession profession;
	private Flag flags;
	private Flag channelFlags;
	private ITerminal terminal;
	private Vitals baseVitals;
	private int damroll;
	private final Attack attack = new Attack(DamType.BASH, "hit");

	public Player() {
		flags = new Flag();
	}

	public Player(String name) {

		super(name);

		flags = new Flag();
	}

	public void addCommand(IExecutable cmd, Argument argument) {
		synchronized (commandQueue) {
			commandQueue.add(new java.lang.Object[] { cmd, argument });
		}
		dequeCommand();
	}

	public boolean dequeCommand() {
		if (commandQueue.isEmpty())
			return false;

		java.lang.Object[] params;

		synchronized (commandQueue) {
			params = commandQueue.remove();
		}

		IExecutable cmd = (IExecutable) params[0];
		Argument arg = (Argument) params[1];

		cmd.execute(this, arg);

		return true;
	}

	@ManyToOne(targetEntity = Account.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "account_id", nullable = false)
	public Account getAccount() {
		return this.account;
	}

	@Embedded
	public Vitals getBaseVitals() {
		return baseVitals;
	}

	@Column(name = "channel_flags", nullable = false)
	public Flag getChannelFlags() {
		return channelFlags;
	}

	@Override
	@Column(name = "damroll")
	public int getDamroll() {
		return damroll;
	}

	@Column(name = "flags", nullable = false)
	public Flag getFlags() {
		return flags;
	}

	@Override
	@Transient
	public Attack getAttack() {
		return attack;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jennings.ryan.jMUD.character.Character#getLongDescr()
	 */
	@Override
	@Transient
	public String getLongDescr() {
		return getName() + " is here.";
	}

	@Override
	@Transient
	public int getPeriod() {
		return SECOND * 4;
	}

	@ManyToOne(targetEntity = Profession.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "profession_id")
	public Profession getProfession() {
		return this.profession;
	}

	@Override
	@Transient
	public String getShortDescr() {
		return getName();
	}

	@Transient
	public ITerminal getTerminal() {
		return terminal;
	}

	@Override
	@Transient
	public int getThac0() {
		return getProfession().getThac0();
	}

	@Override
	@Transient
	public int getThac32() {
		return getProfession().getThac32();
	}

	@Override
	public void onPostLoad(PostLoadEvent event) throws HibernateException {
		this.setVitals(this.getBaseVitals());
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public void setBaseVitals(Vitals value) {
		baseVitals = value;
	}

	@Override
	public void setDamroll(int value) {
		damroll = value;
	}

	public void setProfession(Profession profession) {
		this.profession = profession;
	}

	@Override
	public void setShortDescr(String value) {

	}

	public void setTerminal(ITerminal terminal) {
		this.terminal = terminal;
	}

	@Override
	public boolean tick() {

		return false;
	}

	@Override
	public void write(java.lang.Object value) {
		getTerminal().write(value);
	}

	@Override
	public void writef(String fmt, java.lang.Object... args) {
		getTerminal().writef(fmt, args);
	}

	@Override
	public void writeln() {
		getTerminal().writeln();
	}

	@Override
	public void writeln(java.lang.Object line) {
		getTerminal().writeln(line);
	}

	@Override
	public void writelnf(String fmt, java.lang.Object... args) {
		getTerminal().writelnf(fmt, args);
	}

	@ManyToOne(targetEntity = Room.class)
	@JoinColumn(name = "room_id")
	protected long getLastRoom() {
		return getRoom() == null ? Room.getDefault().getId() : getRoom()
				.getId();
	}

	protected void setChannelFlags(Flag bits) {
		channelFlags = bits;
	}

	protected void setFlags(Flag bits) {
		flags = bits;
	}

	protected void setLastRoom(Room value) {
		setRoom(value == null ? Room.getDefault() : value);
	}
}
