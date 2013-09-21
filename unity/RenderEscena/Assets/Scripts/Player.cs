using UnityEngine;
using System.Collections;

public class Player : MonoBehaviour {

	public float a;
	public float b;
	public float c;
	public float d;
	public float e;

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
			initX + c * Mathf.Sin(a * tnow),
			transform.position.y,
			initY + d * Mathf.Sin(b * tnow)
		);
	}
}
