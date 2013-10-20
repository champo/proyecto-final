using UnityEngine;
using System.Collections;

public class Player : MonoBehaviour {

	public float a;
	public float b;

	private float t0;
	private float tnow;
	private float initX;
	private float initY;
	
	void Start() {
		t0 = Time.fixedTime;
		initX = transform.position.x;
		initY = transform.position.z;
	}

	void FixedUpdate () {
		tnow = (Time.fixedTime - t0);
		transform.position = new Vector3(
			initX + a * tnow,
			transform.position.y,
			initY + b * tnow
		);
	}
}
